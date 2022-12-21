/*
 * - Copyright of Starter Code: Prof. Kevin Andrea, George Mason University.  All Rights Reserved
 * - Copyright of Student Code: Jafar Moukalled
 * - Date: Aug 2022
*/

/* Name: Jafar Moukalled 
 * G01110547
 */

// System Includes
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/time.h>
#include <pthread.h>
#include <sched.h>
// Local Includes
#include "avan_sched.h"
#include "vm_support.h"
#include "vm_process.h"

/*max cmd size*/
#define SIZE 50 

/* Helper function to increment the skips to each node in a queue*/
void avan_increment_all(queue_header_t * queue) {
  process_node_t *node = queue->head;

  while(node != NULL) {
    node->skips++;
    node = node->next;
  }
}

/* Helper function for inserting a node into a queue 
 * In PID ascending order.
 */
void avan_insert_PID_order(queue_header_t *queue, process_node_t *node) {
  process_node_t *temp = queue->head; /*to iterate through the list*/

  /*check if node is invalid*/
  if(node == NULL) {
    printf("Process is invalid.\n");
    return;
  }

  if(temp == NULL) {
    /*this will be the first process*/
    queue->head = node;
    queue->head->next = NULL;
  }
  else {
    while(temp != NULL) {
      if(temp->pid < node->pid) {
        node->next = temp->next;
        temp->next = node;
        break;
      }
      /*go to next link*/
      temp = temp->next;
    }
  }
}

/* Initialize the avan_header_t Struct
 * Follow the specification for this function.
 * Returns a pointer to the new avan_header_t or NULL on any error.
 */
avan_header_t *avan_create() {
  avan_header_t* avanHeader = NULL;
  avanHeader = malloc(sizeof(avan_header_t));

  if(avanHeader == NULL) {
    return NULL;
  }
  else {
    /* allocate each struct pointer*/
    avanHeader->ready_queue = malloc(sizeof(queue_header_t));
    avanHeader->suspended_queue = malloc(sizeof(queue_header_t));
    avanHeader->terminated_queue = malloc(sizeof(queue_header_t));

    /* initialize heads of struct pointers*/
    avanHeader->ready_queue->head = NULL;
    avanHeader->suspended_queue->head = NULL;
    avanHeader->terminated_queue->head = NULL;

    /* initialize each count var in struct pointers*/
    avanHeader->ready_queue->count = 0;
    avanHeader->suspended_queue->count = 0;
    avanHeader->terminated_queue->count = 0;

    return avanHeader; // Replace Me with Your Code!
  }
}

/* Adds a process into the appropriate singly linked list.
 * Follow the specification for this function.
 * Returns a 0 on success or a -1 on any error.
 */
int avan_insert(avan_header_t *header, process_node_t *process) {
  int check = 0;
  int mask = 0x06;

  /*check for error*/
  if(process == NULL) {
    printf("The process being inserted is null!\n");
    return -1;
  }

  /* set flags in process to the ready state*/
  check = (process->flags) & mask;

  if(check == mask) {
    process->flags ^= mask;
  }

  if(check == 0x02) {
    process->flags ^= 0x02;
  }

  if(check == 0x04) {
    process->flags ^= 0x04;
  } 

  process->flags |= 0x01;

  /*insert into ready queue*/
  avan_insert_PID_order(header->ready_queue, process);
  header->ready_queue->count++;
  return 0; // Replace Me with Your Code!
}

/* Move the process with matching pid from Ready to Suspended queue.
 * Follow the specification for this function.
 * Returns a 0 on success or a -1 on any error (such as process not found).
 */
int avan_suspend(avan_header_t *header, pid_t pid) {
  process_node_t* process = header->ready_queue->head->next; /* this is the pointer that will hold the matching process*/
  process_node_t* prev = header->ready_queue->head; /* previous node to properly remove the process from the list*/

  /*check if the head is the matching process*/
  if(prev->pid == pid) {
    /*set the new head to the next node*/
    process = prev;
    header->ready_queue->head = header->ready_queue->head->next;
    process->next = NULL;
    header->ready_queue->count--;
  }
  else {
    /*iterate through ready queue until correct process found*/
    while(process != NULL) {
      if(process->pid == pid) {
        /*remove from ready queue*/
        prev->next = prev->next->next;
        process->next = NULL;
        header->ready_queue->count--;
      }
      else {
        /*iterate*/
        process = process->next;
        prev = prev->next;
      }
    } /*end while*/
  }

  /*check if process was found*/
  if(process == NULL) {
    printf("Process not found.\n");
    return -1;
  }

  /*now set the flags to a suspended state*/
  process->flags >>= 3;
  process->flags <<= 3;
  process->flags |= 0x00000002;

  /*insert process into suspended queue*/
  avan_insert_PID_order(header->suspended_queue, process);
  header->suspended_queue->count++;

  return 0; // Replace Me with Your Code!
}

/* Move the process with matching pid from Suspended to Ready queue.
 * Follow the specification for this function.
 * Returns a 0 on success or a -1 on any error (such as process not found).
 */
int avan_resume(avan_header_t *header, pid_t pid) {
  process_node_t* process = header->suspended_queue->head->next; /* this is the pointer that will hold the matching process*/
  process_node_t* prev = header->suspended_queue->head; /* previous node to properly remove the process from the list*/

  /*check if the head is the matching process*/
  if(prev->pid == pid) {
    /*set the new head to the next node*/
    process = prev;
    header->suspended_queue->head = header->suspended_queue->head->next;
    process->next = NULL;
  }
  else {
    /*iterate through ready queue until correct process found*/
    while(process != NULL) {
      if(process->pid == pid) {
        /*remove from suspended queue*/
        prev->next = prev->next->next;
        process->next = NULL;
        header->suspended_queue->count--;
      }
      else {
        /*iterate*/
        process = process->next;
        prev = prev->next;
      }
    } /*end while*/
  }

  /*check if process was found*/
  if(process == NULL) {
    printf("Process not found.\n");
    return -1;
  }

  /* set flags in process to the ready state*/
  process->flags >>= 3;
  process->flags <<= 3;
  process->flags |= 0x00000001;

  /*insert into ready queue*/
  avan_insert_PID_order(header->ready_queue, process);
  header->ready_queue->count++;

  return 0; // Replace Me with Your Code!
}

/* Insert the process in the Terminated Queue and add the Exit Code to it.
 * Follow the specification for this function.
 * Returns a 0 on success or a -1 on any error.
 */
int avan_quit(avan_header_t *header, process_node_t *node, int exit_code) {
  /*null check*/
  if(node == NULL) {
    printf("Process is invalid.\n");
    return -1;
  }

  /*change flags to terminated state*/
  node->flags >>= 3;
  node->flags <<=3;
  node->flags |= 0x00000004;

  /*clear current exit_code in process*/
  node->flags <<= 28;
  node->flags >>= 28;

  /*mask*/
  node->flags |= (exit_code << 4);

  /*insert into terminated queue*/
  avan_insert_PID_order(header->terminated_queue, node);
  header->terminated_queue->count++;

  return 0; // Replace Me with Your Code!
}

/* Move the process with matching pid from Ready to Terminated and add the Exit Code to it.
 * Follow the specification for this function.
 * Returns its exit code (from flags) on success or a -1 on any error.
 */
int avan_terminate(avan_header_t *header, pid_t pid, int exit_code) {
  process_node_t* process = header->ready_queue->head->next; /* this is the pointer that will hold the matching process*/
  process_node_t* prev = header->ready_queue->head; /* previous node to properly remove the process from the list*/

  /*check ready queue first*/
  if(prev->pid == pid) {
    /*set the new head to the next node*/
    process = prev;
    header->ready_queue->head = header->ready_queue->head->next;
    process->next = NULL;
    header->ready_queue->count--;
  }
  else {
    /*iterate through ready queue until correct process found*/
    while(process != NULL) {
      if(process->pid == pid) {
        /*remove from ready queue*/
        prev->next = prev->next->next;
        process->next = NULL;
        header->ready_queue->count--;
      }
      else {
        /*iterate*/
        process = process->next;
        prev = prev->next;
      }
    } /*end while*/
  }

  /* if process is NULL, means that process isnt in ready queue*/
  /* now to check in suspended queue*/
  if(process == NULL) {
    process = header->suspended_queue->head->next;
    prev = header->suspended_queue->head;

    /*check if head has the process*/
    if(prev->pid == pid) {
    /*set the new head to the next node*/
    process = prev;
    header->suspended_queue->head = header->suspended_queue->head->next;
    process->next = NULL;
    header->suspended_queue->count--;
    }
    else {
      /*iterate through ready queue until correct process found*/
      while(process != NULL) {
        if(process->pid == pid) {
          /*remove from suspended queue*/
          prev->next = prev->next->next;
          process->next = NULL;
          header->suspended_queue->count--;
        }
        else {
          /*iterate*/
          process = process->next;
          prev = prev->next;
        }
      } /*end while*/
    }
  } /*end process check*/

  /* check if process was not found*/
  if(process == NULL) {
    printf("Process not found.\n");
    return -1;
  }

  /*change flags to terminated state*/
  process->flags >>= 3;
  process->flags <<=3;
  process->flags |= 0x00000004;

  /*clear current exit_code in process*/
  process->flags <<= 28;
  process->flags >>= 28;

  /*mask*/
  process->flags |= (exit_code) << 4;

  /*insert into terminated queue*/
  avan_insert_PID_order(header->terminated_queue, process);
  header->terminated_queue->count++;

  return exit_code; // Replace Me with Your Code!
}

/* Create a new process_node_t with the given information.
 * - Malloc and copy the command string, don't just assign it!
 * Follow the specification for this function.
 * Returns the process_node_t on success or a NULL on any error.
 */
process_node_t *avan_new_process(char *command, pid_t pid, int priority, int critical) {
  /*allocate new process and initialize members*/
  process_node_t *process = NULL;
  process = malloc(sizeof(process_node_t));

  if(process == NULL) {
    printf("Allocation failed.\n");
    return NULL;
  }

  process->flags = 0x00000001;
  process->priority = priority;
  
  /*set critical bit*/
  if(critical != 0) {
    process->flags |= 0x00000008;
  }

  /*clear exit code*/
  process->flags &= 0xFFFFFFFF;
  process->skips = 0;
  process->pid = pid;

  if(command == NULL) {
    return NULL;
  }else {
    strncpy( process->cmd, command, MAX_CMD);
  }

  return process; // Replace Me with Your Code!
}

/* Schedule the next process to run from Ready Queue.
 * Follow the specification for this function.
 * Returns the process selected or NULL if none available or on any errors.
 */
process_node_t *avan_select(avan_header_t *header) {
  process_node_t* process = NULL; /* this is the pointer that will hold the matching process*/
  process_node_t* prev = header->ready_queue->head; /* previous node to properly remove the process from the list*/
  process_node_t* min = NULL; /*this is the node with the lowest priority*/
  int crit = 0; /*this is to check if the flag has a critical bit*/

  /* check if list is empty*/
  if(prev == NULL) {
    return NULL;
  }
  else {
    crit = prev->flags;
  }

  /*check if there is only one node in the  list*/
  if(prev->next == NULL) {
    process = prev;
    header->ready_queue->head = NULL;
    process->next = NULL;
    process->skips = 0;

    /*increment all skips by 1*/
    avan_increment_all(header->ready_queue);
    header->ready_queue->count--;
    return process;
  }

  /*check head for crit flag*/
  if((crit & 0x08) == 0x08) {
    /*remove from ready queue*/
    process = prev;
    header->ready_queue->head = header->ready_queue->head->next;
    process->next = NULL;
    process->skips = 0;

    /*increment all skips by 1*/
    avan_increment_all(header->ready_queue);
    header->ready_queue->count--;
    return process;
  }

  process = prev->next;
  /*check other nodes for critical bit*/
  while(process != NULL) {
    crit = process->flags;
    if((crit & 0x08) == 0x08) {
      /*remove from ready queue*/
      prev->next = prev->next->next;
      process->next = NULL;
      process->skips = 0;

      /*increment all skips by 1*/
      avan_increment_all(header->ready_queue);
      header->ready_queue->count--;
      return process;
    }
    process = process->next;
    prev = prev->next;
  }

  /*no critical bits found, now check for starving processes*/
  process = header->ready_queue->head->next;
  prev = header->ready_queue->head;

  /*starting with the head*/
  if(prev->skips >= MAX_SKIPS) {
    /*remove from ready queue*/
    process = prev;
    header->ready_queue->head = header->ready_queue->head->next;
    process->next = NULL;
    process->skips = 0;

    /*increment all skips by 1*/
    avan_increment_all(header->ready_queue);
    header->ready_queue->count--;
    return process;
  }

  while(process != NULL) {
    if(process->skips >= MAX_SKIPS) {
      /*remove from ready queue*/
      prev->next = prev->next->next;
      process->next = NULL;
      process->skips = 0;

      /*increment all skips by 1*/
      avan_increment_all(header->ready_queue);
      header->ready_queue->count--;
      return process;
    }
    process = process->next;
    prev = prev->next;
  }

  /*no starving processes found, now look for lowest priority number*/
  process = header->ready_queue->head->next;
  prev = header->ready_queue->head;

  /*start off with this as min*/
  min = prev;

  /*in this case, min is the prioritized node
  * whenever min is assigned a new process, it means that new process is more prioritized
  */
  while(process != NULL) {
    if(process->priority < min->priority){
      min = process;
    }
    
    if(process->priority == min->priority) {
      if(process->pid < min->pid) {
        min = process;
      }
    }
    process = process->next;
    prev = prev->next;
  }

  /*after the correct node has been chosen, go through the queue one last time to remove it*/
  process = header->ready_queue->head->next;
  prev = header->ready_queue->head;

  /*check if the head is the matching process*/
  if(prev->pid == min->pid) {
    /*set the new head to the next node*/
    process = prev;
    header->ready_queue->head = header->ready_queue->head->next;
    process->next = NULL;
    process->skips = 0;

    /*increment all skips by 1*/
    avan_increment_all(header->ready_queue);
    header->ready_queue->count--;
    return process;
  }
  else {
    /*iterate through ready queue until correct process found*/
    while(process != NULL) {
      if(process->pid == min->pid) {
        /*remove from ready queue*/
        prev->next = prev->next->next;
        process->next = NULL;
        process->skips = 0;

        /*increment all skips by 1*/
        avan_increment_all(header->ready_queue);
        header->ready_queue->count--;
        return process;
      }
      else {
        /*iterate*/
        process = process->next;
        prev = prev->next;
      }
    } /*end while*/
  }
  /*the code should never reach here, so placing an error statement here*/
  printf("Error:...How did I get here?\n");
  return NULL; // Replace Me with Your Code!
}

/* Returns the number of items in a given queue_header_t
 * Follow the specification for this function.
 * Returns the number of processes in the list or -1 on any errors.
 */
int avan_get_size(queue_header_t *ll) {
  return ll->count; // Replace Me with Your Code!
}

/* Frees all allocated memory in the avan_header_tr */
void avan_cleanup(avan_header_t *header) {
  process_node_t *temp = NULL;
  process_node_t *trash = NULL;

  /*start with the ready queue*/
  temp = header->ready_queue->head;
  while(temp != NULL) {
    trash = temp;
    temp = temp->next;
    free(trash);
  }

  /*then the suspended queue*/
  temp = header->suspended_queue->head;
  while(temp != NULL) {
    trash = temp;
    temp = temp->next;
    free(trash);
  }

  /*then the terminated queue*/
  temp = header->terminated_queue->head;
  while(temp != NULL) {
    trash = temp;
    temp = temp->next;
    free(trash);
  }

  /*now free the queues*/
  free(header->ready_queue);
  free(header->suspended_queue);
  free(header->terminated_queue);

  /*finally.. *drum roll* free the header!*/
  free(header);

}

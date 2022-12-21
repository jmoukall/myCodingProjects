/* Task Controller for a customly created shell OS.
*
*  Author: Jafar Moukalled
*  G01110547
*/
#include <sys/wait.h>
#include "taskctl.h"
#include "parse.h"
#include "util.h"

/* Constants */
#define DEBUG 1
static const char *task_path[] = { "./", "/usr/bin/", NULL };

/*struct for linked list for task management*/
typedef struct task_node {
    int task_num;   
    pid_t pid;
    int status;
    int exit_code;
    char cmd[MAXLINE];
    struct task_node* next;
} tasks;

/* --- GLOBAL VARIABLES --- */

tasks* head = NULL; /*head of linked list*/
pid_t temp_pid;     /*temp global pid variable to store across functions*/
int temp_task_num = 0;  /*temp task number to store easily across functions*/

/*boolean flags to state whether the current process is killed, in the background, or suspended*/
bool killed = false, bg = false, suspended = false; 

/*HELPER FUNCTION PROTOTYPES*/
void kitc_new_task(char* cmd);
void kitc_list_tasks();
void kitc_purge_task(int task_num);
tasks* kitc_remove_node(int task_num);
void kitc_exec_command(char *argv[], Instruction inst);
tasks* kitc_find_node(int task_num);
void kitc_exec_bg(char *argv[], Instruction inst);
void signal_handler(int sig);
void kitc_pipe(int task_num1, int task_num2);
void kitc_kill_instruction(int task_num);
void kitc_suspend_instruction(int task_num);
void kitc_resume_instruction(int task_num);

/* The entry of your task controller program */
int main() {
    char cmdline[MAXLINE];        /* Command line */
    char *cmd = NULL;
    struct sigaction sa;        /*Signal handler */
    sa.sa_handler = &signal_handler;

    /* Intial Prompt and Welcome */
    log_kitc_intro();
    log_kitc_help();

    /* Shell looping here to accept user command and execute */
    while (1) {
        char *argv[MAXARGS+1];        /* Argument list */
        Instruction inst;           /* Instruction structure: check parse.h */

        sigaction(SIGINT, &sa, 0);
        sigaction(SIGTSTP, &sa, 0);

        /* Print prompt */
        log_kitc_prompt();

        /* Read a line */
        // note: fgets will keep the ending '\n'
	    errno = 0;
        if (fgets(cmdline, MAXLINE, stdin) == NULL) {
            if (errno == EINTR) {
                continue;
            }
            exit(-1);
        }

        if (feof(stdin)) {  /* ctrl-d will exit text processor */
          exit(0);
        }

        /* Parse command line */
        if (strlen(cmdline)==1)   /* empty cmd line will be ignored */
          continue;     

        cmdline[strlen(cmdline) - 1] = '\0';        /* remove trailing '\n' */

        cmd = malloc(strlen(cmdline) + 1);          /* duplicate the command line */
        snprintf(cmd, strlen(cmdline) + 1, "%s", cmdline);

        /* Bail if command is only whitespace */
        if(!is_whitespace(cmd)) {
            initialize_command(&inst, argv);    /* initialize arg lists and instruction */
            parse(cmd, &inst, argv);            /* call provided parse() */

            if (DEBUG) {  /* display parse result, redefine DEBUG to turn it off */
                debug_print_parse(cmd, &inst, argv, "main (after parse)");
	        }

            /* After parsing: your code to continue from here */
            /*================================================*/

            if(strcmp(inst.instruct, "help") == 0) {/*help command handling*/
                log_kitc_help();
            }
            else if(strcmp(inst.instruct, "quit") == 0) { /*quit command handling*/
                log_kitc_quit();
                exit(0);
            }
            else if(strcmp(inst.instruct, "list") == 0) {/*list command handling*/
                kitc_list_tasks();
            }
            else if(strcmp(inst.instruct, "purge") == 0) {/*purge command handling*/
               kitc_purge_task(inst.num);
            }
            else if(strcmp(inst.instruct, "exec") == 0) {/*exec command handling*/
                kitc_exec_command(argv, inst);
            }
            else if(strcmp(inst.instruct, "bg") == 0) { /*bg command handling*/
                kitc_exec_bg(argv, inst);
            }
            else if(strcmp(inst.instruct, "pipe") == 0) { /*pipe command handling*/
                kitc_pipe(inst.num, inst.num2);
            }
            else if(strcmp(inst.instruct, "kill") == 0) { /*kill command handling*/
                kitc_kill_instruction(inst.num);
            }
            else if(strcmp(inst.instruct, "suspend") == 0) {/*suspend command handling*/
                kitc_suspend_instruction(inst.num);
            }
            else if(strcmp(inst.instruct, "resume") == 0) {/*resume command handling*/
                kitc_resume_instruction(inst.num);
            }
            else {
                /*new task entered*/
                kitc_new_task(cmd);
            }
        }  // end if(!is_whitespace(cmd))

	free(cmd);
	cmd = NULL;
        free_command(&inst, argv);
    }  // end while(1)

    return 0;
}  // end main()

/*This function adds a new task when a new command or instruction has been enetered*/
void kitc_new_task(char* cmd) {

    /*check if head is empty*/
    if(head == NULL) {
        /*allocate memory*/
        head = malloc(sizeof(tasks));

        /*new task will be task 1*/
        head->status = LOG_STATE_READY;
        strncpy(head->cmd, cmd, MAXLINE);
        head->task_num = 1;
        head->exit_code = 0;
        head->pid = 0;
        head->next = NULL;

        /*call log*/
        log_kitc_task_init(head->task_num, cmd);
        return;
    }

    /*create new node for task info*/
    tasks* newTask = malloc(sizeof(tasks));

    /*create temp node to iterate*/
    tasks* temp = head;
    while(temp != NULL) {
        /*insert into next null node if incrementing task checks fail*/
        if(temp->next == NULL) {
            /*make temp->next the next task*/
            newTask->status = LOG_STATE_READY;
            strncpy(newTask->cmd, cmd, MAXLINE);
            newTask->task_num = temp->task_num + 1;
            newTask->exit_code = 0;
            newTask->pid = 0;
            newTask->next = NULL;

            temp->next = newTask;

            /*call log*/
            log_kitc_task_init(newTask->task_num, cmd);
            return;
        }
        
        /*check if temp->next task num is one higher than temp task num*/
        if(temp->next->task_num == (temp->task_num + 1)) {
            /*increment*/
            temp = temp->next;
        }
        else {
            /*new task will be insterted after temp*/
            newTask->status = LOG_STATE_READY;
            strncpy(newTask->cmd, cmd, MAXLINE);
            newTask->task_num = temp->task_num + 1;
            newTask->exit_code = 0;
            newTask->pid = 0;
            newTask->next = NULL;

            temp->next = newTask;

            /*call log*/
            log_kitc_task_init(newTask->task_num, cmd);
        }
    }/*end while*/
}/*end kitc_new_task function*/

/*This function lists all of the tasks in the task manager*/
void kitc_list_tasks() {
    int num_tasks = 0;
    tasks* temp = head; /*temp node to iterate through list*/

    /*iterate through once to find number of tasks*/
    while(temp!=NULL) {
        num_tasks++;
        temp = temp->next;
    }

    /*call log for num tasks*/
    log_kitc_num_tasks(num_tasks);

    /*reset temp node and print info for each task*/
    temp = head;
    while(temp != NULL) {
        /*call log info for each node*/
        log_kitc_task_info(temp->task_num, temp->status, temp->exit_code, temp->pid, temp->cmd);
        temp = temp->next;
    }
} /*end kitc_list_tasks function*/

/*This function purges a task based on the task number*/
void kitc_purge_task(int task_num) {
    tasks* temp = NULL; /*temp node to purge properly*/

    /*find node with matching task num*/
    temp = kitc_find_node(task_num);

    /*check to see if loop found a node*/
    if(temp == NULL) {
        /*call log*/
        log_kitc_task_num_error(task_num);

        return;
    }
    else {
        /*check if task is busy*/
        if((temp->status == LOG_STATE_SUSPENDED) || (temp->status == LOG_STATE_RUNNING)) {
            /*call log*/
            log_kitc_status_error(task_num, temp->status);

            return;
        }
        else {
            /*task is good to remove*/
            head = kitc_remove_node(task_num);

            /*call log*/
            log_kitc_purge(task_num);
        }
    }
} /*end kitc_purge_task function*/

/*Helper function to remove a node from a linked list*/
tasks* kitc_remove_node(int task_num) {
    tasks* temp = NULL;
    tasks* prev = NULL;

    /*check if wanted node is at the head*/
    if(head->task_num == task_num) {
        temp = head;
        head = head->next;
        free(temp);

        return head;
    }

    /*iterate to find node*/
    temp = head->next;
    prev = head;

    /*break when node is found*/
    while(temp != NULL) {
        if(temp->task_num == task_num) {
            break;
        }
        else {
            temp = temp->next;
            prev = prev->next;
        }
    }

    /*remove node*/
    prev->next = temp->next;
    free(temp);

    /*return new head*/
    return head;
}

/*This function executes a command in the foreground*/
void kitc_exec_command(char *argv[], Instruction inst) {
    tasks* temp = NULL;
    Instruction tempinst; /*a temp instruction variable to fill argv again without emptying inst*/
    int task_num = inst.num, file1, file2, statusChild;
    char path1[MAXLINE] = "./";
    char path2[MAXLINE] = "/usr/bin/";


    temp_task_num = task_num;
    struct sigaction sa;        /*Signal handler for ctrlZ and ctrlC*/
    sa.sa_handler = &signal_handler;
    sigset_t block;
    sigemptyset(&block);
    sigaddset(&block, SIGCHLD); /*block sigchld so it does not interfere*/
    sa.sa_mask = block;

    /*initialize bool flags to false*/
    killed = false;
    bg = false;
    suspended = false;

    /*find the node corresponding to task_num*/
    temp = kitc_find_node(task_num);

    /*call parse again for argv*/
    initialize_command(&tempinst, argv); 
    parse(temp->cmd, &tempinst, argv);  

    /*if node was not found*/
    if(temp == NULL) {
        /*call log*/
        log_kitc_task_num_error(task_num);
        return;
    }

    /*check if task is busy*/
    if((temp->status == LOG_STATE_SUSPENDED) || (temp->status == LOG_STATE_RUNNING)) {
        /*call log*/
        log_kitc_status_error(task_num, temp->status);
        return;
    }

    /*concatenate everything onto the paths*/
    strncat(path1, argv[0], MAXLINE); 
    strncat(path2, argv[0], MAXLINE); 

    /*fork process into child and parent*/
    temp_pid = fork();
    setpgid(0,0);

    if(temp_pid == 0) {
        /*child*/

        /*check if redirection is involved*/
        if((inst.infile != NULL) && (inst.outfile == NULL)) {
            /*infile is only specified*/
            file1 = open(inst.infile, O_RDWR);
            log_kitc_redir(task_num, LOG_REDIR_IN, inst.infile);
            dup2(file1, STDIN_FILENO);
        } 
        else if((inst.outfile != NULL) && (inst.infile == NULL)) {
            /*outfile is only specified*/
            file2 = open(inst.outfile, O_RDWR);
            log_kitc_redir(task_num, LOG_REDIR_OUT, inst.outfile);
            dup2(file2, STDOUT_FILENO);
        }
        else if((inst.outfile != NULL) && (inst.infile != NULL)) {
            /*both infile and outfile are specified*/
            file1 = open(inst.infile, O_RDWR);
            file2 = open(inst.outfile, O_RDWR);
            log_kitc_redir(task_num, LOG_REDIR_IN, inst.infile);
            log_kitc_redir(task_num, LOG_REDIR_OUT, inst.outfile);
            dup2(file1, STDIN_FILENO);
            dup2(file2, STDOUT_FILENO);
        }
        else {
            /*continue normally*/
        }

        /*execute commands in both paths*/
        execv(path1, argv);
        execv(path2, argv);

        /*terminate child process*/
        log_kitc_exec_error(temp->cmd);
        exit(1);
    }
    else {
        /*parent*/
        log_kitc_status_change(task_num, temp_pid, LOG_FG, temp->cmd, LOG_START);

        /*change status to running*/
        temp->status = LOG_STATE_RUNNING;

        /*reap child*/
        sigaction(SIGINT, &sa, 0);
        sigaction(SIGTSTP, &sa, 0);

        /*reap here only if it is not killed or suspended*/
        if(!killed || !suspended) {
            waitpid(temp_pid, &statusChild, 0);
        }

        /*check if child was reaped normally*/
        if(!suspended && !killed && WEXITSTATUS(statusChild)) {
            /*invalid command*/
            temp->exit_code = WEXITSTATUS(statusChild);
            temp->pid = temp_pid;
            temp->status = LOG_STATE_FINISHED;
            log_kitc_status_change(task_num, temp_pid, LOG_FG, temp->cmd, LOG_TERM);
        }
        else if(!suspended && !killed && WIFEXITED(statusChild)) {
            /*terminated normally*/
            temp->exit_code = WEXITSTATUS(statusChild);
            temp->pid = temp_pid;
            temp->status = LOG_STATE_FINISHED;
            log_kitc_status_change(task_num, temp_pid, LOG_FG, temp->cmd, LOG_TERM);
        }
    }
    temp_task_num = 0; /*reset task_num*/
} /*end kitc_exec_command function*/

/*Helper function to find a node in the task list corresponding to matching task_num*/
tasks* kitc_find_node(int task_num) {
    /*check for empty list*/
    if(head == NULL) {
        return NULL;
    }
    tasks* temp = head;

    /*iterate to find the same task_num*/
    temp = head;
    while(temp != NULL) {
        if(temp->task_num == task_num) {
            break;
        }
        else {
            temp = temp->next;
        }
    }

    /*check to see if it found the node*/
    if(temp == NULL) {
        return NULL;
    }
    else {
        return temp;
    }
}

/*This function executes a command in the background*/
void kitc_exec_bg(char *argv[], Instruction inst) {
    tasks* temp = NULL;
    Instruction tempinst;
    int task_num = inst.num, file1, file2;
    char path1[MAXLINE] = "./";
    char path2[MAXLINE] = "/usr/bin/";

    temp_task_num = task_num;
    bg = true;

    /*need sigaction to handle SIGCHLD*/
    struct sigaction sa;
    sa.sa_handler = &signal_handler;

    /*find the node corresponding to task_num*/
    temp = kitc_find_node(task_num);

    /*call parse again for argv*/
    initialize_command(&tempinst, argv); 
    parse(temp->cmd, &tempinst, argv);  

    /*if node was not found*/
    if(temp == NULL) {
        /*call log*/
        log_kitc_task_num_error(task_num);
        return;
    }

    /*check if task is busy*/
    if((temp->status == LOG_STATE_SUSPENDED) || (temp->status == LOG_STATE_RUNNING)) {
        /*call log*/
        log_kitc_status_error(task_num, temp->status);
        return;
    }

    /*concatenate everything onto the paths*/
    strncat(path1, argv[0], MAXLINE); 
    strncat(path2, argv[0], MAXLINE); 

    /*fork processes*/
    temp_pid = fork();
    setpgid(0,0);

    if(temp_pid == 0) {
        /*child*/

        /*check if redirection is involved*/
        if((inst.infile != NULL) && (inst.outfile == NULL)) {
            /*infile is only specified*/
            file1 = open(inst.infile, O_RDWR);
            log_kitc_redir(task_num, LOG_REDIR_IN, inst.infile);
            dup2(file1, STDIN_FILENO);
        } 
        else if((inst.outfile != NULL) && (inst.infile == NULL)) {
            /*outfile is only specified*/
            file2 = open(inst.outfile, O_RDWR);
            log_kitc_redir(task_num, LOG_REDIR_OUT, inst.outfile);
            dup2(file2, STDOUT_FILENO);
        }
        else if((inst.outfile != NULL) && (inst.infile != NULL)) {
            /*both infile and outfile are specified*/
            file1 = open(inst.infile, O_RDWR);
            file2 = open(inst.outfile, O_RDWR);
            log_kitc_redir(task_num, LOG_REDIR_IN, inst.infile);
            log_kitc_redir(task_num, LOG_REDIR_OUT, inst.outfile);
            dup2(file1, STDIN_FILENO);
            dup2(file2, STDOUT_FILENO);
        }
        else {
            /*continue normally*/
        }

        /*execute args*/
        execv(path1, argv);
        execv(path2, argv);

        /*terminate child process*/
        log_kitc_exec_error(temp->cmd);
        exit(1);
    }
    else {
        /*parent*/
        log_kitc_status_change(task_num, temp_pid, LOG_BG, temp->cmd, LOG_START);
        
        /*change status to running*/
        temp->status = LOG_STATE_RUNNING;

        /*check if child was completed normally*/
        sigaction(SIGCHLD, &sa, 0);
    }
}/*end kitc_exec_bg function*/

/*This function redirects output from one task to the input of another*/
void kitc_pipe(int task_num1, int task_num2) {
    tasks* task1, *task2 = NULL;
    int ary[2], fd_write, fd_read, status;
    pid_t pid1, pid2;
    Instruction inst1, inst2;
    char *argv1[MAXLINE], *argv2[MAXLINE], path1[MAXLINE], path2[MAXLINE];

    /*check if tasks are identical*/
    if(task_num1 == task_num2) {
        log_kitc_pipe_error(task_num1);
        return;
    }

    /*find each task node*/
    task1 = kitc_find_node(task_num1);
    task2 = kitc_find_node(task_num2);

    /*check if task number was invalid*/
    if((task1 == NULL) && (task2 != NULL)) {
        /*invalid task1*/
        log_kitc_task_num_error(task_num1);
        return;
    }
    else if((task1 != NULL) && (task2 == NULL)) {
        /*invlaid task2*/
        log_kitc_task_num_error(task_num2);
        return;
    }
    else if((task1 == NULL) && (task2 == NULL)) {
        /*invalid task1 and task2*/
        log_kitc_task_num_error(task_num1);
        log_kitc_task_num_error(task_num2);
        return;
    }

    /*check if either task is busy*/
    if((task1->status == LOG_STATE_RUNNING) || (task1->status == LOG_STATE_SUSPENDED)) {
        /*task1 is busy*/
        log_kitc_status_error(task1->task_num, task1->status);
        return;
    }
    else if((task2->status == LOG_STATE_RUNNING) || (task2->status == LOG_STATE_SUSPENDED)) {
        /*task 2 is busy*/
        log_kitc_status_error(task2->task_num, task2->status);
    }

    /*create pipe and check validity*/
    if(pipe(ary) == -1) {
        log_kitc_file_error(task_num1, LOG_FILE_PIPE);
        return;
    }
    else {
        /*pipe creation successful*/
        log_kitc_pipe(task_num1, task_num2);

        /*assign FD to read and write*/
        fd_read = ary[0];
        fd_write = ary[1];
    }

    /*set paths*/
    strncpy(path1, string_copy(task_path[0]), MAXLINE);
    strncpy(path2, string_copy(task_path[1]), MAXLINE);

    /*parse cmd to fill argv*/
    initialize_command(&inst1, argv1);
    initialize_command(&inst2, argv2);
    parse(task1->cmd, &inst1, argv1);
    parse(task2->cmd, &inst2, argv2);

    /*concatenate paths with args*/
    strncat(path1, string_copy(argv1[0]), MAXLINE);
    strncat(path2, string_copy(argv1[0]), MAXLINE);

    /*start fork and execute one process in the background*/
    pid1 = fork();
    setpgid(0,0);

    if(pid1 == 0) {
        /*child*/
        dup2(fd_write, STDOUT_FILENO);
        close(fd_read);
        close(fd_write);

        /*try both paths*/
        execv(path1, argv1);
        execv(path2, argv1);

        /*return exit code 1 if fails*/
        log_kitc_exec_error(task1->cmd);
        exit(1);
    }
    else {
        /*parent of pid1*/
        /*call log*/
        log_kitc_status_change(task_num1, pid1, LOG_BG, task1->cmd, LOG_START);

        /*re set the paths*/
        strncpy(path1, string_copy(task_path[0]), MAXLINE);
        strncpy(path2, string_copy(task_path[1]), MAXLINE);

        /*re concatenate paths with args*/
        strncat(path1, string_copy(argv2[0]), MAXLINE);
        strncat(path2, string_copy(argv2[0]), MAXLINE);

        /*start fork of process 2*/
        pid2 = fork();
        setpgid(0,0);

        if(pid2 == 0) {
            /*child of pid2*/
            dup2(fd_read, STDIN_FILENO);
            close(fd_write);
            close(fd_read);

            /*try both paths*/
            execv(path1, argv2);
            execv(path2, argv2);

            /*if exec failed*/
            log_kitc_exec_error(task2->cmd);
            exit(1);
        }
        else {
            /*parent of pid2*/
            log_kitc_status_change(task_num2, pid2, LOG_FG, task2->cmd, LOG_START);

            /*close pipe and reap foreground process*/
            close(fd_write);
            close(fd_read);
            waitpid(task2->pid, &status, 0);

            /*check if child was reaped normally*/
            if(WEXITSTATUS(status)) {
                /*change for task 1 too*/
                task1->exit_code = 0;
                task1->pid = pid1;
                task1->status = LOG_STATE_FINISHED;
                log_kitc_status_change(task_num1, pid1, LOG_BG, task1->cmd, LOG_TERM);

                /*invalid command*/
                task2->exit_code = WEXITSTATUS(status);
                task2->pid = pid2;
                task2->status = LOG_STATE_FINISHED;
                log_kitc_status_change(task_num2, pid2, LOG_FG, task2->cmd, LOG_TERM);
            }
            else if(WIFEXITED(status)) {
                /*terminated normally*/
                task1->exit_code = WEXITSTATUS(status);
                task1->pid = pid1;
                task1->status = LOG_STATE_FINISHED;
                log_kitc_status_change(task_num1, pid1, LOG_BG, task1->cmd, LOG_TERM);

                /*do for both tasks*/
                task2->exit_code = WEXITSTATUS(status);
                task2->pid = pid2;
                task2->status = LOG_STATE_FINISHED;
                log_kitc_status_change(task_num2, pid2, LOG_FG, task2->cmd, LOG_TERM);
            } 
        }
    }
} /*end kitc_pipe function*/

/*This function sends a SIGINT signal to the process to kill it*/
void kitc_kill_instruction(int task_num) {
    tasks* task = NULL;
    killed = true;

    /*find node in task list*/
    task = kitc_find_node(task_num);

    /*check if node was not found*/
    if(task == NULL) {
        log_kitc_task_num_error(task_num);
        return;
    }

    /*check if the task is idle*/
    if((task->status == LOG_STATE_FINISHED) || (task->status == LOG_STATE_KILLED) || (task->status == LOG_STATE_READY)) {
        log_kitc_status_error(task_num, task->status);
        return;
    }

    /*call log*/
    log_kitc_sig_sent(LOG_CMD_KILL, task->task_num, temp_pid);

    /*kill the process*/
    kill(temp_pid, SIGINT);
    waitpid(temp_pid, NULL, 0);

    /*change process info*/
    task->status = LOG_STATE_KILLED;
    task->pid = temp_pid;
    task->exit_code = 0;

    /*in order to determine if background process or foreground*/
    if(!bg) {
        log_kitc_status_change(task->task_num, temp_pid, LOG_FG, task->cmd, LOG_TERM_SIG);
    }
    else {
        log_kitc_status_change(task->task_num, temp_pid, LOG_BG, task->cmd, LOG_TERM_SIG);
    }
}

/*This function sends a SIGTSTP signal to the process to suspend it*/
void kitc_suspend_instruction(int task_num) {
    tasks* task = NULL;
    suspended = true;

    /*find node in the list*/
    task = kitc_find_node(task_num);

    /*check if node was found*/
    if(task == NULL) {
        log_kitc_task_num_error(task_num);
        return;
    }

    /*check if the task is idle*/
    if((task->status == LOG_STATE_FINISHED) || (task->status == LOG_STATE_KILLED) || (task->status == LOG_STATE_READY)) {
        log_kitc_status_error(task_num, task->status);
        return;
    }

    /*call log*/
    log_kitc_sig_sent(LOG_CMD_SUSPEND, task->task_num, temp_pid);

    /*suspend process*/
    kill(temp_pid, SIGTSTP);
    waitpid(temp_pid, NULL, 0);
    
    /*change process info*/
    task->status = LOG_STATE_SUSPENDED;
    task->pid = temp_pid;

    /*in order to determine if background process or foreground*/
    if(!bg) {
        log_kitc_status_change(task->task_num, temp_pid, LOG_FG, task->cmd, LOG_SUSPEND);
    }
    else {
        log_kitc_status_change(task->task_num, temp_pid, LOG_BG, task->cmd, LOG_SUSPEND);
    }

}/*end kitc_suspend_instruction function*/

/*This function sends a SIGCONT signal to the process to continue execution*/
void kitc_resume_instruction(int task_num) {
    tasks* task = NULL;
    int status;

    /*find node in the list*/
    task = kitc_find_node(task_num);

    /*check if node was found*/
    if(task == NULL) {
        log_kitc_task_num_error(task_num);
        return;
    }

    /*check if the task is idle*/
    if((task->status == LOG_STATE_FINISHED) || (task->status == LOG_STATE_KILLED) || (task->status == LOG_STATE_READY)) {
        log_kitc_status_error(task_num, task->status);
        return;
    }

    /*call log*/
    log_kitc_sig_sent(LOG_CMD_RESUME, task_num, temp_pid);

    /*in order to determine if background process or foreground*/
    if(!bg) {
        log_kitc_status_change(task->task_num, temp_pid, LOG_FG, task->cmd, LOG_RESUME);
    }
    else {
        log_kitc_status_change(task->task_num, temp_pid, LOG_BG, task->cmd, LOG_RESUME);
    }

    /*resume the process*/
    kill(temp_pid, SIGCONT);
    waitpid(temp_pid, &status, 0);

    /*check if child was reaped normally*/
    if(WEXITSTATUS(status)) {
        /*invalid command*/
        task->exit_code = WEXITSTATUS(status);
        task->pid = temp_pid;
        task->status = LOG_STATE_FINISHED;
        log_kitc_status_change(task_num, temp_pid, LOG_FG, task->cmd, LOG_TERM);
    }
    else if(WIFEXITED(status)) {
        /*terminated normally*/
        task->exit_code = WEXITSTATUS(status);
        task->pid = temp_pid;
        task->status = LOG_STATE_FINISHED;
        log_kitc_status_change(task_num, temp_pid, LOG_FG, task->cmd, LOG_TERM);
    }

}

/*This function is the signal handler for the program*/
void signal_handler(int sig) {
    if(sig == SIGINT) {
        /*ctrlC was pressed*/
        log_kitc_ctrl_c();

        /*if not in the background, kill the process*/
        if(!bg && (temp_task_num != 0)) {
            kitc_kill_instruction(temp_task_num);
        }
    }
    else if(sig == SIGTSTP) {
        /*ctrlZ was pressed*/
        log_kitc_ctrl_z();

        /*if not in the background, suspend the process*/
        if(!bg && (temp_task_num != 0)) {
            kitc_suspend_instruction(temp_task_num);
        }
    }
    else if(sig == SIGCHLD) {
        /*child was terminated properly*/
        int status;
        if(temp_task_num != 0) {
            tasks* temp = kitc_find_node(temp_task_num);
            if(!suspended) {
                waitpid(temp_pid, &status, 0);
            }

            /*check if status is good*/
            if(!killed && (status != 0)) {
                /*invalid command*/
                temp->exit_code = 1;
                temp->status = LOG_STATE_FINISHED;
                temp->pid = temp_pid;
                log_kitc_status_change(temp->task_num, temp_pid, LOG_BG, temp->cmd, LOG_TERM);
                temp_task_num = 0;
            }
            else if(!killed && (status == 0)) {
                /*terminated normally*/
                temp->exit_code = 0;
                temp->status = LOG_STATE_FINISHED;
                temp->pid = temp_pid;
                log_kitc_status_change(temp->task_num, temp_pid, LOG_BG, temp->cmd, LOG_TERM);
                temp_task_num = 0;
            }
        }
    }
} /*end signal_handler function*/

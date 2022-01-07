#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<ctype.h>
#include "orderList.h"

void printMenu()
{
    printf("\n*****************************************************");
    printf("\n\n* Welcome to the Campus Starship Robots Delivery");
    printf("\n* Central Dispatch System");
    printf("\n\n*****************************************************");
}

/*compares strings for alphabetical ordering*/
int strcmpi(char *s, char *t)
{
    while(*s && tolower(*s) == tolower(*t))
    {
        s++;
        t++;
    }
    return tolower(*s) - tolower(*t);
}

/*allocates memory with a check for successful allocation*/
void *dmalloc(size_t size)
{
    void *p = malloc(size);
    if(!p)
    {
        printf("\nMemory allocation failed.\n");
        exit(1);
    }
    return p;
}

/*creates an instance of an orderList struct*/
orderList *createItem()
{
	int num;
	char buffer[20];
	orderList *list = dmalloc(sizeof(orderList));
	list->head = NULL;
	list->count = 0;

	/*in this case, this will continuously ask the user to
	* enter more food items until the user presses *enter*/
   	while(1)
	{
		printf("\nFood Item: ");
		fgets(buffer, 20, stdin);
		if(strcmp(buffer, "\n") == 0)
		{
			break;
		}
		num = insert(buffer, &list);

		/*error handling*/
		if(num == 0)
		{
			printf("\nFood item not stored properly.");
		}

		(list->count)++;
	}
    return list;
}

/*insert a new food node*/
int insert(char *str, orderList **s)
{
	foodNode *current = dmalloc(sizeof(foodNode));

	current->data = str;
	current->next = (*s)->head;
	(*s)->head = current;

	/*check if food item was stored properly*/
	if(strcmp((*s)->head->data, str) == 0)
	{
		return 1;
	}
	else
	{
		return 0;
	}
}

/*print food items*/
void printItems(orderList *s)
{
	foodNode *temp = (s->head);
	/*print food items until there aren't any more*/
	while(temp != NULL)
	{
		printf("\nFood item: %s", temp->data);
		temp = temp->next;
	}
}

#include "orderList.c"

typedef struct _robotOrder
{
    unsigned int robotNum;
    orderList *data;
    char *deliverTo;
    char *restaurant;
    struct _robotOrder *next;
}robotOrder;

int main()
{
	robotOrder *deliveryList = NULL;
	robotOrder *currentOrder;
	char buffer[50];
	char c[5];

	/*print starting menu*/
	printMenu();
	/*ask user if they want to make a delivery order*/
	while(1)
	{
        printf("\nNew delivery order? (y/n) ");
        fgets(c, 5, stdin);
        switch(c[0])
        {
       		case 'y':
            	/*some code here for new robot order*/
            	currentOrder = dmalloc(sizeof(robotOrder));
		
				/*ask for robot number*/
           		printf("\nRobot number: ");
            	fgets(buffer, 50, stdin);
            	currentOrder->robotNum = atoi(buffer);

				/*ask for address*/
				printf("\nAddress to deliver to: ");
				fgets(buffer, 50, stdin);
				currentOrder->deliverTo = buffer;

				/*ask for what restaurant*/
				printf("\nRestaurant Name: ");
				fgets(buffer, 50, stdin);
				currentOrder->restaurant = buffer;

				/*ask for food for order using foodNode*/
				currentOrder->data = createItem();

				/*algorithm for linked list of robotOrders*/
				currentOrder->next = deliveryList;
				deliveryList = currentOrder;
                break;
			case 'n':
				/*take out newline char from buffer*/
				buffer[strcspn(buffer, "\n")] = 0;

                // code here to end the program and print all orders
				printf("\nList of Deliveries:");
				robotOrder *temp = deliveryList;
				while(temp != NULL)
				{
					/*print current order and food items*/
					printf("\nRobot number %d: ", temp->robotNum);
					printf("Delivery order from %s ", temp->restaurant);
					printf("has %d item(s)", temp->data->count);
					
					/*print food items*/
					printItems(temp->data);

					temp = temp->next;
				}
				goto EXIT_PROGRAM;
			default:
                printf("\nInvalid Input. Please try again.");
        }
    }

	EXIT_PROGRAM:
		free(deliveryList);
		free(currentOrder);
		printf("\n");
    return 0;
}

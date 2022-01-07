
typedef struct _foodNode
{
    char *data; // Food Item Name
    struct _foodNode *next;
}foodNode;

typedef struct _orderList
{
    foodNode *head; // Pointer to first food item for the order (alphabetical)
    int count;      // Number of food items in the order
}orderList;


/*function prototypes*/
void printMenu();
int strcmpi(char *s, char *t);
void *dmalloc(size_t size);
orderList *createItem();
int insert(char *str, orderList **s);
void printItems(orderList *s);

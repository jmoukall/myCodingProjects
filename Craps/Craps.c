/*	Jafar Moukalled	G01110547
 *	CS 262, Lab Section 223
 *	Project 1
 */

#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include<time.h>

#define INIT 100

void printIntro();
int roll();
void playGame();
int bet();
int passLine(int bet, int balance);
int dontPassLine(int bet, int balance);

int main(void)
{
	int seed = 0;
		
	/*print the intro message*/
	printIntro();

	/*the user will be asked to type a seed*/
	printf("Please enter a number for the seed:");
	scanf("%d", &seed);
	srand(seed);
	/*the game starts!*/
	playGame();

	return 0;
}

void printIntro()
{
	printf("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
	printf("Welcome to the game of Craps!\n");
	printf("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n");
}

int roll()
{
	int die1 = 0;
	int die2 = 0;
	int sum = 0;
	char c;
	*int Buffer = *(sizeof(c));

	die1 = (rand()%6) +1;
	die2 = (rand()%6) +1;
	sum = die1 + die2;
	
	printf("Press 'enter' to roll the dice!");	
	sscanf("%c", &c, Buffer);
	if(c == '\n')
	{	
		printf("\nRolling . . .\n");
		printf("Dice 1: %d\n", die1);
		printf("Dice 2: %d\n", die2);
		printf("Roll Total: %d\n", sum);
	}
	return sum;
}

void playGame()
{
	int b = 0;
	int balance = INIT;
	int choice = 0;
	char choice2 = 'a';
	while(1)
	{	
		printf("\nYour balance is $%d\n", balance);	
		b = bet();

		/*ask user to pass line or don't pass line*/
		printf("\nEnter your option:\n");
		printf("1 - Pass Line\n");
		printf("2 - Do not Pass Line\n");
		printf("3 - Quit\n");
		printf("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		scanf("%d", &choice);

		switch(choice)
		{
			case 1:
				balance = passLine(b, balance);
				break;
			case 2:
			 	balance = dontPassLine(b, balance);
				break;
			case 3:
				goto exit_loop;
		}
		if((balance != 0) && (balance > 0))
		{
			printf("Do you want to keep playing?(Y/N)\n");
			scanf(" %c", &choice2);
			if((choice2 ==  'Y') || (choice2 == 'y'))
			{
				/*nothing to see here, just following the loop*/
			}
			else
			{
				goto exit_loop;
			}
		}
		else
		{
			printf("You ran out of funds!\n");
			break;
		}
	}

exit_loop:
	printf("\nGAME OVER\n");
	printf("Your final balance is $%d\n\n", balance);
}

int passLine(int bet, int balance)
{
	int r = 0;
	int point = 0;
	char choice = 'a';
	printf("You chose 'Pass Line'\n");
	r = roll();
	if((r == 7) || (r == 11))
	{
		printf("Congratulations! You have just won $%d\n", bet);
		balance += bet;
		return balance;
	}
	else if((r == 2) || (r == 3) || (r == 12))
	{
		printf("You lost $%d :(\n", bet);
		balance -= bet;
		return balance;
	}
	else
	{
		printf("Would you like to double your bet?(Y/N)\n");
		scanf(" %c", &choice);
		if(choice == 'Y' || choice == 'y')
		{
			bet = bet *2;
			printf("Your bet is $%d\n", bet);
		}
		point = r;
		
		while(1)
		{
			printf("Your point is %d\n", point);
			r = roll();
			if(r == point)
			{
				printf("Congratulations! You just won $%d!\n", bet);
				balance += bet;
				break;
			}
			else if(r == 7)
			{
				printf("You lost $%d :(\n", bet);
				balance -= bet;
				break;
			}
			else
			{	
				/*continue rolling until a 7 or the point is acheieved*/	
			}
		}
		return balance;

	}


}

int dontPassLine(int bet, int balance)
{
	int r = 0;
        int point = 0;
        char choice = 'a';
        printf("You chose 'Don't Pass Line'\n");
        r = roll();
        if((r == 2) || (r == 3) || (r == 12))
        {
                printf("Congratulations! You have just won $%d\n", bet);
                balance += bet;
		return balance;
        }
        else if((r == 7) || (r == 11))
        {
                printf("You lost $%d :(\n", bet);
                balance -= bet;
		return balance;
        }
        else
        {
                printf("Would you like to double your bet?(Y/N)\n");
                scanf(" %c", &choice);
                if(choice == 'Y' || choice == 'y')
                {
                        bet = bet *2;
                        printf("Your bet is %d\n", bet);
                }
                point = r;

                while(1)
                {
                        printf("Your point is %d\n", point);
                        r = roll();
                        if(r == 7)
                        {
                                printf("Congratulations! You just won $%d!\n", bet);
                                balance += bet;
                                break;
                        }
                        else if(r == point)
                        {
                                printf("You lost $%d :(\n", bet);
                                balance -= bet;
                                break;
                        }
                        else
                        {
                                /*continue rolling until a 7 or the point is acheieved*/
                        }
                }
		return balance;

        }


}

int bet()
{
	int bet = 0;
	while(1)
        {
                printf("Place a bet bewteen 5 - 100 inclusive: $");
                scanf("%d", &bet);

                if((bet < 5) || (bet > 100))
                {
                        printf("\nInavlid bet placed, please try again.\n");
                }
                else
                {
                	break;
		}
	}
	return bet;
}

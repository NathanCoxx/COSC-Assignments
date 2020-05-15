import java.util.Scanner;
public class AirlineReservationProblem
{
    public static void main(String[] args){
        Scanner in=new Scanner(System.in);
        int[][]seating=new int[20][4];

        //initialize array
        for(int row = 0; row < 20; row ++) {
            for (int col = 0; col < 4; col++)
                seating[row][col] = 0;
        }

        int choice=0;
        while(choice!=5)
        {
            System.out.println("Enter 1 for first class, 2 for business class, 3 for economy class, 4 to display the available seats, and 5 to quit:");
            choice=in.nextInt();
            switch(choice)
            {
                case 1:
                    if(IsFlightFull(seating))
                        System.out.println("This Flight is Full, Next Flight leaves in Three Hours.");
                    else if(IsFirstClassFull(seating))
                         System.out.println("First Class is Full. Try Business Class or Economy Class.");
                    else
                        MakeFirstClassReservation(seating);
                    break;
                case 2:
                    if(IsFlightFull(seating))
                         System.out.println("This Flight is Full, Next Flight leaves in Three Hours.");
                    else if(IsBusinessClassFull(seating))
                        System.out.println("Business Class is Full. Try First Class or Economy Class.");
                    else
                        MakeBusinessClassReservation(seating);
                    break;
                case 3:
                    if(IsFlightFull(seating))
                         System.out.println("This Flight is Full, Next Flight leaves in Three Hours.");
                    else if(IsEconomyClassFull(seating))
                        System.out.println("Economy Class is Full. Try First Class or Business Class.");
                    else
                        MakeEconomyClassReservation(seating);
                    break;
                case 4:
                    DisplayArray(seating);
                    break;
            case 5:
            }
        }
    }
    private static void MakeFirstClassReservation(int[][] seating)
    {
        boolean seatFound = false;
        for(int row = 0; row < 3 && !seatFound; row++) {
            for(int col = 0; col < 4 && !seatFound; col++){
                if(seating[row][col] == 0){
                    seatFound = true;
                    seating[row][col] = 1;
                    System.out.println("Your seat has been reserved! \n " +
                            "Boarding Pass: First Class - Row " + (row+1) + " Column " + (col+1));

                }

            }
        }
    }
    private static void MakeBusinessClassReservation(int[][] seating)
    {
        boolean seatFound = false;
        for(int row = 3; row < 6 && !seatFound; row++) {
            for (int col = 0; col < 4 && !seatFound; col++) {
                if (seating[row][col] == 0) {
                    seatFound = true;
                    seating[row][col] = 1;
                    System.out.println("Your seat has been reserved! \n " +
                            "Boarding Pass: Business Class - Row " + (row+1) + " Column " + (col+1));

                }

            }
        }
    }
    private static void MakeEconomyClassReservation(int[][] seating)
    {
        boolean seatFound = false;
        for(int row = 7; row < 20 && !seatFound; row++) {
            for (int col = 0; col < 4 && !seatFound; col++) {
                if (seating[row][col] == 0) {
                    seatFound = true;
                    seating[row][col] = 1;
                    System.out.println("Your seat has been reserved! \n " +
                            "Boarding Pass: Economy - Row " + (row+1) + " Column " + (col+1));

                }

            }
        }
    }
    public static boolean IsFirstClassFull(int seating[][])
    {
        for (int row = 0; row <= 3;row++)
        {
            for (int col = 0; col < seating[row].length;col++)
            {
                if (seating[row][col] == 0)
                    return false;
            }

        }
        return true;
    }


    public static boolean IsBusinessClassFull(int[][] seating)
    {
        for (int row = 3; row <= 6;row++)
        {
            for (int col = 0; col < seating[row].length ;col++)
            {
                if (seating[row][col] == 0)
                    return false;
            }

        }
        return true;
    }
    public static boolean IsEconomyClassFull(int[][] seating)
    {
        for (int row = 7; row < 20;row++)
        {
            for (int col = 0; col < seating[row].length;col++)
            {
                if (seating[row][col] == 0)
                    return false;
            }

        }
        return true;
    }
    public static boolean IsFlightFull(int[][] seating)
    {
        return IsFirstClassFull(seating) && IsBusinessClassFull(seating) && IsEconomyClassFull(seating);
    }
    public static void DisplayArray(int[][] seating) {

        for (int row = 0; row < seating.length; row++)
        {
            for (int col = 0; col < seating[row].length; col++)
                System.out.print(seating[row][col] + " ");
            System.out.println();
        }

    }
}
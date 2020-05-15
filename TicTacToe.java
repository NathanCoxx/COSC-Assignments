import java.util.Scanner;

public class TicTacToeGame {
    public static void main(String[] args)  {

        Scanner in = new Scanner(System.in);
        char[][] board = {{'-','-','-'},{'-','-','-'},{'-','-','-'}};
        int counter = 0;
        int row;
        int column;

        printBoard(board);
        System.out.print("Enter move for team(X) " + counter + ": 'Row' 'Column' ");
        row = in.nextInt() - 1;
        column = in.nextInt() - 1;
        while(!xMove(board, row, column)){
            row = in.nextInt() - 1;
            column = in.nextInt() - 1;
        }
        printBoard(board);

        System.out.print("Enter move for team(O) " + counter + ": 'Row' 'Column' ");
        row = in.nextInt() - 1;
        column = in.nextInt() - 1;
        while(!oMove(board, row, column)){
            row = in.nextInt() - 1;
            column = in.nextInt() - 1;
        }
        printBoard(board);

    }
    private static void printBoard(char[][] board){
        for(int row = 0; row < 3; row++){
            for(int col = 0; col < 3; col++)
                System.out.print(board[row][col] + " ");
            System.out.println();
        }
    }
    private static boolean xMove(char[][] board, int row, int col){
        boolean success;
        if(board[row][col] == '-') {
            board[row][col] = 'X';
            success = true;
        }
        else {
            System.out.println("This spot is already taken, try again:");
            success = false;
        }
        return success;
    }
    private static boolean oMove(char[][] board, int row, int col){
        boolean success;
        if(board[row][col] == '-') {
            board[row][col] = 'O';
            success = true;
        }
        else {
            System.out.println("This spot is already taken, try again:");
            success = false;
        }
        return success;
    }

}
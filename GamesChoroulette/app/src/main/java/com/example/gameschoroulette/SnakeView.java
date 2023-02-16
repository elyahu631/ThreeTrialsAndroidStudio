package com.example.gameschoroulette;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.LinkedList;

public class SnakeView extends View {

    MediaPlayer mediaPlayer1 = MediaPlayer.create(getContext(), R.raw.game_eat_sound);
    MediaPlayer mediaPlayer2 = MediaPlayer.create(getContext(), R.raw.snake_hissing);


    int growth;
    int points;
    int levelHelper;

    private final GestureDetector gestures;
    private Direction direction;
    private LinkedList<Point> list;


    private final Handler handler =new Handler(Looper.getMainLooper());
    private final Runnable time;

    private int column, row; // column and row where the head of the snake is located
    private int colFruit, rowFruit; // column and row where the fruit is located

    private boolean gameOn = true; // set to false when game ends
    private int growthIndicates = 0; // indicates the number of squares the snake should grow
    private int squareSide; // size of each square in pixels
    private final int tableWidth =25;
    private int tableHeight;//framesHeight is calculated based on the height of the device


    // enum is a special data type that allows you to define a set of named constants here I set the directions
    private enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    //The class point with a constructor
    static class Point {
        int x, y;
        
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }



    //הבנאי מאתחל את התצוגה על ידי הגדרת GestureDetector
    // לאיתור קלט משתמש ויצירת Runnable שיבוצע שוב ושוב כדי לקדם את מצב
    // המשחק. ה-Runnable מזיז את הנחש צעד אחד בכיוון הנוכחי, מוסיף קטע חדש
    // לחלק הקדמי של גוף הנחש, מסיר את הקטע האחרון אם הנחש לא אכל פרי (הנחש לא גדל)
    //, מקטין את מונה הצמיחה אם הנחש גדל , בודק אם הנחש יצא מחוץ לתחום,
    // מבטל את התצוגה כדי להפעיל ציור מחדש, ומתזמן את האיטרציה הבאה של
    // הלולאה אם המשחק עדיין פעיל.


    // constructor that takes a context and attributes and calls the constructor of the parent class
    // also initializes some private fields, creates a linked list to represent the snake and sets the starting direction of the snake
    public SnakeView(Context context, @Nullable AttributeSet attributes) {
        // Call the superclass constructor and pass in the context and attribute set
        super(context, attributes);

        // Create a new GestureDetector to detect user gestures, and pass in a custom listener
        gestures  = new GestureDetector(this.getContext(),new GestureListener());

        // Create a new Runnable that will be executed repeatedly by a handler
        time =new Runnable() {
            @Override
            public void run() {
                // Move the snake one step in the current direction
                switch (direction) {
                    case RIGHT:
                        // If the snake is moving right, don't allow it to turn back to the left
                        column = DoNotGoBack1(list.get(0).x , list.get(1).x ,column);
                        break;
                    case LEFT:
                        // If the snake is moving left, don't allow it to turn back to the right
                        column = DoNotGoBack2(list.get(0).x , list.get(1).x ,column);
                        break;
                    case UP:
                        // If the snake is moving up, don't allow it to turn back down
                        row = DoNotGoBack2(list.get(0).y , list.get(1).y ,row);
                        break;
                    case DOWN:
                        // If the snake is moving down, don't allow it to turn back up
                        row = DoNotGoBack1(list.get(0).y , list.get(1).y ,row);
                        break;
                }
                // Move the snake forward by adding a new head to the list
                StepOn();
                //We insert the coordinate of the head of the snake in the list
                list.addFirst(new Point(column, row));

                // If the snake did not eat a fruit and is not growing, remove the last segment
                if (!verifyEatsFruit() && growthIndicates == 0) {
                    //if we are not in the coordinate of the fruit the viper should not grow
                    //we delete the last node of the List
                    //this makes the List still have the same amount of nodes
                    list.remove(list.size() - 1);
                } else {
                    // If growing is greater than zero, we must make the snake grow By not deleting the new tail
                    // and Resetting the snake's growth to 0
                    if (growthIndicates > 0)
                        growthIndicates--;
                }
                checkOutBoard();

                // Invalidate the view to trigger a redraw
                invalidate();

                // If the game is still active, schedule the next iteration of the loop
                if (gameOn)
                    handler.postDelayed(this,250 - points);
            }
        };

        // Start the game loop
        Start();
    }



   //  The following two functions calculate the next row or column position of the snake based on its current direction of movement.

    public int DoNotGoBack1(int p1,int p2,int num){
        if(p1 < p2) {
            return --num;
        }
        return ++num;
    }

    public int DoNotGoBack2(int p1,int p2,int num){
        if(p1 > p2) {
            return ++num;
        }
        return --num;
    }

    //initializes the snake game.
    public void Start(){
        list = new LinkedList<>(); // creates a new linked list to store the positions of the snake
        direction = Direction.RIGHT;//  sets the initial direction of the snake to the right.
        growthIndicates = 0; //  initializes a variable that keeps track of how much the snake will grow when it eats fruit.
        gameOn = true; // Set active to true - game is currently active.

        // adds four points to the linked list to represent the initial position of the snake.
        list.add(new Point(4, 5));
        list.add(new Point(3, 5));
        list.add(new Point(2, 5));
        list.add(new Point(1, 5));

        //we indicate the location of the viper's head
        column = 4;
        row = 5;

        GenerateFruitCoordinate(); // randomly generates the location of the fruit that the snake will eat.

        handler.removeCallbacksAndMessages(null);
        // creates a new handler and post a delayed runnable that will update the game state every 100ms.
        handler.postDelayed(time,100);
        points = 0;
        growth = 1; // sets a variable that indicates how much the snake will grow when it eats fruit.
        levelHelper = 10;// Auxiliary variable for level calculation
    }


    //We control if the head of the viper is inside its body
    private void StepOn() {
        for (Point p : list) {
            if (p.x == column && p.y == row) {
                gameOn = false;
                break;
            }
        }
    }

    // check if we are outside the board region
    private void checkOutBoard() {
        if (column <= 0 || column >= tableWidth  || row <= 0 || row >= tableHeight) {
            gameOn = false;
        }
    }

    private boolean verifyEatsFruit() {
        if (column == colFruit && row == rowFruit) {
            mediaPlayer1.start();
            GenerateFruitCoordinate();
            growthIndicates = growth;
            ScoreAndLevel();
            return true;
        }
        return false;
    }

    private void GenerateFruitCoordinate() {
        do {
            colFruit = 3 + (int) (Math.random() * (tableWidth - 4));
            rowFruit = 4 + (int) (Math.random() * (tableHeight - 4));
        } while (IsFruitInsideSnake());
    }

    private boolean IsFruitInsideSnake() {
            for (Point p : list) {
                if (p.x == colFruit && p.y == rowFruit) {
                    return true;
                }
            }
        return false;
    }



    public void ScoreAndLevel(){
        points += growth;
        if(points >= levelHelper){
            growth++;
            levelHelper *= growth;
        }
    }

    // method is used to calculate the size of the squares that will make up the playing field
    // based on the width of the screen and the number of squares desired in each row
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Get the display metrics
        DisplayMetrics displaymetrics = getResources(). getDisplayMetrics();

        // Calculate the size of each square by dividing the screen width by the table width
        // for example 1080 / 25 = 43
        squareSide = displaymetrics.widthPixels/ tableWidth;

        // Calculate the table height by dividing the screen height by the size of each square
        // for example 1920 / 43 = 44 = squares that can fit vertically on the device screen.
        tableHeight =displaymetrics.heightPixels/ squareSide;

        // Set the measured dimensions to the full screen size using the display metrics
        setMeasuredDimension(displaymetrics.widthPixels, displaymetrics.heightPixels);
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestures.onTouchEvent(event); // detect the type of gesture made by the user and  Pass the MotionEvent to the GestureDetector

        return true; // mean event has been handled.
    }


    //מחלקה מיוחדת המתעסקת עם אירועי נגיעה במסך
    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mediaPlayer2.start();

            // Calculate the distance in the X and Y axes between the start and end points of the gesture
            float width = Math.abs(e2.getX()- e1.getX());
            float height = Math.abs(e2.getY()-e1.getY());

            // Determine the direction of the swipe based on the longer axis
            if (width>height) {
                if (e2.getX() > e1.getX()) {
                    direction = Direction.RIGHT;
                } else {
                    direction = Direction.LEFT;
                }
            }
            else {
                if (e2.getY() > e1.getY()) {
                    direction = Direction.DOWN;
                } else {
                    direction = Direction.UP;
                }
            }
            // Return true to indicate that the gesture has done
            return true;
        }




        @Override
        public boolean onDoubleTap(MotionEvent e) {
            AreYouWantToRestartTheGame();
            return true;
        }
    }


    public void AreYouWantToRestartTheGame(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you Want to start a new game?");
        builder.setPositiveButton("Yes", (dialog, which) -> Start());
        builder.setNegativeButton("No", (dialog, which) -> {
            // do nothing
        });

        // Set the background and text color of the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(102,0,0)
        ));
        alertDialog.setMessage(Html.fromHtml("<font color='#FFFFFF'>" + "Do you want to Restart the game?" + "</font>"));

        alertDialog.show();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the background of the screen - color = black
        canvas.drawRGB(0,0,0);
        Paint brush1=new Paint();

        // draws the grid on which the game is played - color = dark red
        brush1.setColor(Color.rgb(128,0,0));
        // drawing the vertical lines of the game board
        for(int c = 0; c<= tableWidth +1; c++)
            canvas.drawLine(c* squareSide,0,
                    c* squareSide, tableHeight * squareSide,brush1);
        //drawing the horizontal lines of the game board
        for(int f = 0; f<= tableHeight; f++)
            canvas.drawLine(0,f* squareSide,
                    tableWidth * squareSide + tableWidth,f* squareSide,brush1);

        // draw the snake's body - color = red
        brush1.setColor(Color.rgb(211,41,15));
        for (Point point : list) {
            canvas.drawRect(point.x * squareSide, point.y * squareSide,
                    point.x * squareSide + squareSide -3,
                    point.y * squareSide + squareSide -3,brush1);
        }

        // draw fruit - color = yellow
        brush1.setColor(Color.rgb(238, 186, 48));
        float centerX = (colFruit * squareSide) + (squareSide / 2);
        float centerY = (rowFruit * squareSide) + (squareSide / 2);
        canvas.drawCircle(centerX, centerY, squareSide / 2, brush1);

        // add text view on top of the screen to show the score
        brush1.setTextSize(40);
        brush1.setColor(Color.rgb(230,119,11));
        canvas.drawText("Score:" + points, (tableWidth * squareSide) - squareSide * 6, 40, brush1);
        brush1.setTextSize(40);
        brush1.setColor(Color.rgb(82,208,83));
        canvas.drawText("Level: " + growth, 20, 40, brush1);

        // if the game is over, show a message
        if (!gameOn) {
            AreYouWantToRestartTheGame();
            brush1.setTextSize(50);
            brush1.setColor(Color.RED);
            canvas.drawText("Game Over!", (tableWidth * squareSide) / 2 - squareSide * 5,
                    (tableHeight * squareSide) / 2, brush1);
        }
    }

}
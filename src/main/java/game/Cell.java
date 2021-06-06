package game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell  extends Rectangle {
    public int x,y; //координаты ячейки
    public Ship ship = null;
    public boolean wasShot = false;

    private Field field;//поле

    public Cell(int x, int y, Field field){
        super(30,30);
        this.x = x;
        this.y = y;
        this.field=field;
        setFill(Color.LIGHTGRAY);
        setStroke(Color.BLACK);
    }

    public boolean shoot(){
        wasShot = true;
        setFill(Color.GRAY);

        if (ship !=null){
            ship.hit();
            setFill(Color.RED);
            if (!ship.isAlive()){
                field.ships--;
            }
            return true;
        }
        return false;
    }
}

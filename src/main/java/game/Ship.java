package game;

import javafx.scene.Parent;

public class Ship extends Parent {
    public int type; //тип палубы
    public boolean vert = true; //вертикальное расположение
    private int health; //здоровье

    public Ship (int type, boolean vert){ //конструктор
        this.type=type;
        this.vert=vert;
        health=type;
    }

    public void hit(){
        health--;
    } //если попал по кораблю

    public boolean isAlive(){
        return health > 0;
    } //проверка на состояние корабля
}

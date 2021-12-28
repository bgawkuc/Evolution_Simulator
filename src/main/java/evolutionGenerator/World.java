package evolutionGenerator;

import evolutionGenerator.GUI.App;
import javafx.application.Application;


public class World {
    public static void main(String[] args) {
        try {
            Application.launch(App.class, args);
        }catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }
}

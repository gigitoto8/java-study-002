package app.display;

import java.util.List;

public class DisplayUtil{

    public void showList(List<? extends Displayable> list){

        for(Displayable item :list){
            System.out.println(item.display());
        }
    }
}
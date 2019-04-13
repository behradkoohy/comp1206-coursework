package comp1206.sushi.developmentFiles;

import comp1206.sushi.ClientApplication;
import comp1206.sushi.ServerApplication;

public class LaunchBoth {
    public static void main(String[] argv) {
        System.out.println("Running server");
        ServerApplication.main(argv);
        System.out.println("Running client");
        ClientApplication.main(argv);
    }
}

package api.commands;



import api.Command;

public class TestCommand extends Command{
    public static void main(String[] args) {
        System.out.println("I am a compiled File!");
    }

    @Override
    public void execute() throws Exception {
        // TODO Auto-generated method stu
        
        System.out.println("I am a compiled File! + 1");
    }
    
}

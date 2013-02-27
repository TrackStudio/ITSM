package scripts.before_create_task;

import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.secured.SecuredTaskTriggerBean;

import java.util.ArrayList;
import java.util.List;

public class SetTechSuppHandler implements TaskTrigger {
    public SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) throws GranException {
    	System.out.println("It works!!!");

    	return task;
    }

    private static int foo() {
        int a=1,b=2;
        try {
            return a+b;
        } finally {
            a=10;
            b=20;
           // return a+b;
        }
    }


    private static void foo(String t, String w) {
        System.out.println(t + " " + w);
    }

    public static void main(String[] arg) {

    }
}

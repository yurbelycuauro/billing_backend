import org.springframework.context.annotation.*;
public class Probe {
  public static void main(String[] args) {
    try {
      AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
      ctx.scan("com.mobile.billing");
      ctx.refresh();
      System.out.println("OK");
      ctx.close();
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}

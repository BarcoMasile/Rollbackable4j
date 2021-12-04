package xyz.marcobasile.rollbackable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import xyz.marcobasile.rollbackable.annotation.Action;
import xyz.marcobasile.rollbackable.annotation.Rollback;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class RollbackableApplication {

    public static void main(String[] args) {
        SpringApplication.run(RollbackableApplication.class, args);
    }

}

@Component
class Prova {

    @Action(forExceptions = RuntimeException.class)
    public Object a(String o, Object o1, Integer o2) {
        throw new IllegalStateException("OK");
    }

    @Rollback
    public void b(RuntimeException ex) {
        System.out.println("Funziona: " + ex.getMessage());
    }
}
@Component
class B {
    @Autowired
    Prova p;

    @PostConstruct
    void c() {
        final Object a = p.a(null, null, null);

        System.out.println("Risultato e' null? " + (null == a));
    }
}

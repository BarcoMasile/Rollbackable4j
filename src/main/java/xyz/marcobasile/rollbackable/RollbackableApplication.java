package xyz.marcobasile.rollbackable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import xyz.marcobasile.rollbackable.annotation.Action;
import xyz.marcobasile.rollbackable.annotation.Rollback;
import xyz.marcobasile.rollbackable.config.RollbackableAutoconfiguration;

import javax.annotation.PostConstruct;

@EnableAspectJAutoProxy
@SpringBootApplication
public class RollbackableApplication {

    public static void main(String[] args) {
        SpringApplication.run(RollbackableApplication.class, args);
    }

}

@Component
class Prova {

    @Action(rollback = "prova", forExceptions = RuntimeException.class)
    public Object a() {
        throw new IllegalStateException("OK");
    }

    @Rollback(name = "prova")
    public void b(Throwable ex) {
        System.out.println("Funziona: " + ex.getMessage());
    }
}
@Component
class B {
    @Autowired
    Prova p;

    @PostConstruct
    void c() {
        final Object a = p.a();

        System.out.println("Risultato e' null? " + (null == a));
    }
}

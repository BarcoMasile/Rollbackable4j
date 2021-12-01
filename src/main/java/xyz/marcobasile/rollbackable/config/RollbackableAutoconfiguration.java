package xyz.marcobasile.rollbackable.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import xyz.marcobasile.rollbackable.annotation.Action;
import xyz.marcobasile.rollbackable.annotation.Rollback;

@Configuration
@EnableAspectJAutoProxy
public class RollbackableAutoconfiguration {}

package wang.wangby.trace.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import wang.wangby.annotation.persistence.Id;
import wang.wangby.config.*;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.socket.listener.*;
import wang.wangby.repository.MysqlRepository;
import wang.wangby.repository.jdbc.RoutingDataSource;
import wang.wangby.repostory.Repository;
import wang.wangby.repostory.database.dto.DatabaseInfo;
import wang.wangby.utils.IdWorker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
@Import({BaseToolAutoConfiguration.class,WebAutoConfiguration.class, RepostoryAutoConfiguration.class})
public class TraceAutoConfiguration implements CommandLineRunner {

    @Bean
    public Exchange exchange() {
        return new Exchange();
    }

    @Bean
    public AggTradeListener aggTradeListener() {
        return new AggTradeListener();
    }


    @Bean
    public AccountUpdateListener accountUpdateListener() {
        return new AccountUpdateListener();
    }


    @Bean
    public Market market(Repository repository) {
        return new Market(repository);
    }

    @Autowired
    RoutingDataSource routingDataSource;

    @Bean
    public SocketClient socketClient(
            Exchange exchange,
            AggTradeListener aggTradeListener,
            AccountUpdateListener accountUpdateListener,
            Market market) throws URISyntaxException, InterruptedException {
        List<MessageListener> messageListeners = new ArrayList<>();
        messageListeners.add(aggTradeListener);
        List<AccountListener> accountListeners = new ArrayList<>();
        accountListeners.add(new OrderTradeUpdateListener(market));
        accountListeners.add(accountUpdateListener);
        return new SocketClient(exchange, messageListeners, accountListeners);
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker();
    }

    @Override
    public void run(String... args) throws Exception {
        DatabaseInfo databaseInfo=new DatabaseInfo();
        databaseInfo.setUsername("root");
        databaseInfo.setPassword("root");
        databaseInfo.setUrl("jdbc:mysql://127.0.0.1/trace");
        routingDataSource.addDtasource(databaseInfo.getKey(),databaseInfo);
    }
}

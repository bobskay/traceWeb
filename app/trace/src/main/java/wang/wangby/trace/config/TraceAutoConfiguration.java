package wang.wangby.trace.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import wang.wangby.config.BaseToolAutoConfiguration;
import wang.wangby.config.Beans;
import wang.wangby.config.FileRepositoryAutoConfiguration;
import wang.wangby.config.WebAutoConfiguration;
import wang.wangby.exchange.Exchange;
import wang.wangby.exchange.socket.listener.*;
import wang.wangby.persistence.file.AsynRepository;
import wang.wangby.persistence.file.DataSerializer;
import wang.wangby.persistence.file.FileRepository;
import wang.wangby.persistence.file.config.MyFileProperties;
import wang.wangby.repostory.Repository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
@ConditionalOnClass(FileRepository.class)
@Import({BaseToolAutoConfiguration.class,
        WebAutoConfiguration.class,
        FileRepositoryAutoConfiguration.class})
@EnableConfigurationProperties(MyFileProperties.class)
public class TraceAutoConfiguration {

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

}

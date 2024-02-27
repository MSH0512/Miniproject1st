package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * 멀티쓰레드를 고려한 ConnectionPool singleton pattern 을 사용해서 미리 생성해보았습니다
 * volatile 와 synchronized 를 사용해 thread-safe 한 커넥션을 생성했습니다.
 * Apache 의 Commons DECP 를 사용해서 connection 을 만들었습니다
 * 필요사항  : External Libraries 에 commons-dbcp2-2.11.0.jar 파일을 추가를 해주어야 합니다
 * : ctrl + alt + shift + s --> module --> add jar file
 */
public class DBConnection {
    /**
     * === volatile ===
     * 멀티쓰레스를 쓸 때 thread-safe 상황을 만들기 위해 사용 됩니다.
     * volatile keyword는 Java 변수를 Main Memory에 저장하겠다라는 것을 명시하는 것입니다.
     * 매번 변수의 값을 Read할 때마다 CPU cache에 저장된 값이 아닌 Main Memory에서 읽는 것입니다.
     * 또한 변수의 값을 Write할 때마다 Main Memory에 까지 작성하는 것입니다.
     * <p>
     * Thread-1은 counter값을 증가시키고 있지만 CPU Cache에만 반영되어있고 실제로 Main Memory에는 반영이 되지 않았습니다.
     * 그렇기 때문에 Thread-2는 count값을 계속 읽어오지만 0을 가져오는 문제가 발생합니다.
     * <p>
     * 즉, 두명의 조교가 교수님이 구글드라이브(main memory)로 제공해주신 하나의 파일을 공동 작업하기 위해,
     * 다운로드 하여 본인 컴퓨터(cpu cache) 에서 작성을 수정/작성을 하고 있다고 가정하면,
     * 조교2는 조교1이 수정한 값을 모르기 때문에 공동 작업을 할 때 서로의 최신 값을 모르기 때문에 문제가 생길 수 있습니다.
     * <p>
     * 왜/언제 쓰느냐?
     * --> 단지 변수의 접근(read/write)에 대해서만 정합성 보장
     * --> 단점은 cpu cache 에서 바로 가져오는게 아니라 접근성이 느립니다
     * <p>
     * --> 그렇기 때문에 반드시 멀티쓰레드인 상황에서 쓰는게 좋지만 추후 멀티쓰레드로 바뀔 것을 생각을 해서 미리 구현을 했다.
     * <p>
     * 출처 : https://junghyungil.tistory.com/99
     */

    private static volatile DataSource dataSourceInstance;

    /**
     * === Singleton Pattern ===
     * 싱글톤은 한번 만들고 재사용 하는 것이기 때문에 클래스 밖에서 인스턴스화를 막기 위해 private
     * 싱글턴 패턴(Singleton pattern)을 따르는 클래스는, 생성자가 여러 차례 호출되더라도 실제로 생성되는 객체는 하나이고
     * 최초 생성 이후에 호출된 생성자는 최초의 생성자가 생성한 객체를 리턴한다.
     * 이와 같은 디자인 유형을 싱글턴 패턴이라고 한다.
     */
    protected DBConnection() {
    }


    /**
     * 싱글톤을 사용한 ConnectionPool 로서 DataSource 인스턴스로 return 을 합니다.
     * 커넥션이 필요하다면(null 이면) 생성이 됩니다
     *
     * @return : DataSource instance
     */
    public static DataSource getDataSource() {
        /**
         * === Double-checked locking ===
         * 쓰레드의 안정성을 두번 체크를 해주는 부분입니다. 멀티쓰레드 환경을 고려해서,
         * volatile 과 synchronized 를 사용을 하였기 때문에 먼저 싱글톤 instance 가 null 인지 확인을 해주고,
         * synchronized 를 사용한 후 한번 더 체크를 해줍니다
         *
         * 첫번째 null 값 체크를 해주는 이유는 만약 instance 가 이미 만들어 졌을 경우 overhead 를 막아주기 위함이고,
         * synchronized 의 lock 에 접근을 하지 않고 바로 method 를 종료 시키기에 퍼포먼스적으로 효율적이다.
         *
         *      === overhead ===
         *      오버 헤드는 특정 기능을 수행하는데 드는 간접적인 시간, 메모리 등 자원을 말한다.
         *      예를들어,  10초 걸리는 기능이 간접적인 원인으로 20초걸린다면 오버헤드는 10초가 되는것이다.
         *
         *      === lock ===
         *      쉽게 말하면 다른 Thread 들 보고 기다려(lock)! 내 차례야 하는 것이다.
         *
         * synchronized 에서는 instance 가 생성이 되지 않으면(null 일경우) 블록으로 들어가게 되는데 이는,
         * 멀티쓰레드 상황에서 동시에 여러개의 instance 가 생성이 되는 것을 막아주는 역활을 한다.
         *
         */
        if (dataSourceInstance == null) {
            synchronized (DBConnection.class) {
                if (dataSourceInstance == null) {
                    try {
                        BasicDataSource dataSource = getSource();

                        dataSourceInstance = dataSource;
                    } catch (IOException ee) {
                        System.out.println("DB Connection Failed");
                        ee.printStackTrace();
                    } catch (Exception e) {
                        System.out.println("DB connection Failedd");
                        e.printStackTrace();
                    }
                }
            }
        }
        return dataSourceInstance;
    }

    /**
     * ConnectionPool basic set up
     * <p>
     * current set up : pool_size(1 thread), timeout(10sec)
     *
     * @return
     * @throws IOException
     * @author : Tae Jin Kim
     */

    private static BasicDataSource getSource() throws IOException {
        BasicDataSource dataSource = getBasicDataSource();

        // v0.0.1 TOBE
        // TODO::멀티쓰레드: 추후 멀티 쓰레드 환경을 만들 경우 숫자 바꿔주기
        // 우리는 현재 멀티쓰레드를 하지 않기 때문에 connection pool 사이즈를 1개만 사용 하게 할것이다
        dataSource.setInitialSize(4);
        // 최대값도 1개로 제한을 합니다
        dataSource.setMaxTotal(4);
        // 커넥션 최대 wait 초를 10초로 지정하고 10초를 넘어가면 실패를 합니다.
//        dataSource.setMaxWait(Duration.ofSeconds(3000));
        return dataSource;
    }

    /**
     * .env 파일에서 DB connectoin 에 필요한 정보들을 가지고 옵니다.
     *
     * @return dataSource : db 커넥션에 필요한 정보를 담은 BasicDataSource Type 입니다.
     * 정보의 흐름 : .env 파일 --> Properties --> String --> BasicDataSource
     * --> String 단계를 건너뛰고 바로 BasicDataSource 타입에 Properties 로 가지고 온 정보를 담아도 무방합니다.
     * @throws IOException
     */
    private static BasicDataSource getBasicDataSource() throws IOException {
        BasicDataSource dataSource = new BasicDataSource();
        try {
            // TODO:: .env 파일을 읽어옵니다. 파일 경로는 모든 사람마다 다 다르므로 수정을 해주세요
            FileInputStream fs = new FileInputStream("C:/Refactoring/src/env");
            Properties properties = new Properties();
            properties.load(fs);

            // Properties 로 .env 파일의 값을 받아와서 BasicDataSource 에 옮기기 위해 String 에 옮겨 담아줍니다.
            String host = properties.getProperty("DB_HOST");
            String port = properties.getProperty("DB_PORT");
            String db = properties.getProperty("DATABASE");
            String jdbcUrl = properties.getProperty("JDBC_URL");
            String username = properties.getProperty("USER");
            String password = properties.getProperty("PASSWORD");
            String driver = properties.getProperty("DRIVER");
            String timezone = properties.getProperty("TIMEZONE");


            // .env 파일로 부터 받아온 값들을 dataSource 에 넣어줘서 커넥션을 연결합니다.
            dataSource.setDriverClassName(driver);
            dataSource.setUrl(jdbcUrl + host + port + db + timezone);
            dataSource.setUsername(username);
            dataSource.setPassword(password);

            return dataSource;
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        } catch(NoSuchFieldError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }
}

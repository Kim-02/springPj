package com.dasolsystem.add;

import com.dasolsystem.core.entity.Account;
import com.dasolsystem.core.jparepository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@SpringBootTest
public class accountadd {

    @Autowired
    private AccountRepository accountRepository;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        System.out.println("✅ 모든 데이터 삭제");
    }

    @Test
    void insertTestCase(){
        IntStream.range(1,101).forEach(i ->{
                    Account account = Account.builder()
                            .name("User_"+i)
                            .balance(random.nextLong(1000,10000))
                            .message("Test account "+i)
                            .build();
                    accountRepository.save(account);
                }
                );
        System.out.println("✅ 100개의 테스트 데이터가 삽입되었습니다");
    }

    @Test
    void insertFullTextTestCase() {
        List<String> lines = Arrays.asList(
                "오늘도 또 우리 수탉이 막 쫓기었다.",
                "내가 점심을 먹고 나무를 하러 갈 양으로 나올 때였다.",
                "산으로 올라서려니까 등뒤에서 푸드득 푸드득 하고 닭의 횃소리가 야단이다.",
                "깜짝 놀라서 고개를 돌려 보니 아니나 다르랴 두 놈이 또 얼리었다.",
                "점순네 수탉(대강이가 크고 똑 오소리같이 실팍하게 생긴 놈)이 덩저리 작은 우리 수탉을 함부로 해내는 것이다.",
                "그것도 그냥 해내는 것이 아니라 푸드득하고 면두를 쪼고 물러섰다가 좀 사이를 두고 푸드득하고 모가지를 쪼았다.",
                "이렇게 멋을 부려 가며 여지없이 닦아 놓는다.",
                "그러면 이 못생긴 것은 쪼일 적마다 주둥이로 땅을 받으며 그 비명이 킥, 킥, 할뿐이다.",
                "물론 미처 아물지도 않은 면두를 또 쪼이며 붉은 선혈은 뚝뚝 떨어진다.",
                "이걸 가만히 내려다보자니 내 대강이가 터져서 피가 흐르는 것같이 두눈에서 불이 번쩍 난다. 대뜸 지게 막대기를 메고 달려들어 점순네 닭을 후려칠까 하다가 생각을 고쳐먹고 헛매질로 떼어만 놓았다.",
                "이번에도 점순이가 쌈을 붙여 놨을 것이다.",
                "바짝바짝 내 기를 올리느라고 그랬음에 틀림없을 것이다.",
                "고놈의 계집애가 요새로 들어서 왜 나를 못 먹겠다고 고르게 아르릉거리는지 모른다.",
                "나흘 전 감자건만 하더라도 나는 저에게 조금도 잘못한 것은 없다.",
                "계집애가 나물을 캐러 가면 갔지 남 울타리 엮는 데 쌩이질을 하는 것은 다 뭐냐.",
                "그것도 발소리를 죽여 가지고 등뒤로 살며시 와서,",
                "\"얘! 너 혼자만 일하니?\"",
                "하고 긴치 않는 수작을 하는 것이다.",
                "어제까지도 저와 나는 이야기도 잘 않고 서로 만나도 본체만척체고 이렇게 점잖게 지내던 터이련만 오늘로 갑작스레 대견해졌음은 웬일인가.",
                "항차 망아지만 한 계집애가 남 일하는 놈 보구",
                "\"그럼 혼자 하지 떼루 하듸?\"",
                "내가 이렇게 내배앝는 소리를 하니까,",
                "\"너 일하기 좋니?\"",
                "또는, \"한여름이나 되거든 하지 벌써 울타리를 하니?\"",
                "잔소리를 두루 늘어놓다가 남이 들을까봐 손으로 입을 틀어막고는 그 속에서 깔깔댄다.",
                "별로 우스울 것도 없는데 날씨가 풀리더니 이 놈의 계집애가 미쳤나 하고 의심하였다.",
                "게다가 조금 뒤에는 제 집께를 할금할금 돌아보더니 행주치마의 속으로 꼈던 바른손을 뽑아서 나의 턱밑으로 불쑥 내미는 것이다.",
                "언제 구웠는지 더운 김이 홱 끼치는 굵은 감자 세 개가 손에 뿌듯이 쥐였다.",
                "\"느 집엔 이거 없지?\"",
                "하고 생색있는 큰소리를 하고는 제가 준 것을 남이 알면은 큰일날테니 여기서 얼른 먹어 버리란다.",
                "그리고 또 하는 소리가,",
                "\"너 봄감자가 맛있단다.\"",
                "\"난 감자 안 먹는다. 너나 먹어라.\"",
                "나는 고개도 돌리지 않고 일하던 손으로 그 감자를 도로 어깨 너머로 쑥 밀어 버렸다.",
                "그랬더니 그래도 가는 기색이 없고, 뿐만 아니라 쌔근쌔근하고 심상치 않게 숨소리가 점점 거칠어진다.",
                "이건 또 뭐야 싶어서 그때에야 비로소 돌아다보니 나는 참으로 놀랐다.",
                "우리가 이 동네에 들어온 것은 근 삼년째 되어오지만 여태껏 가무잡잡한 점순이의 얼굴이 이렇게까지 홍당무처럼 새빨개진 법이 없었다.",
                "게다 눈에 독을 올리고 한참 나를 요렇게 쏘아보더니 나중에는 눈물까지 어리는 것이 아니냐.",
                "그리고 바구니를 다시 집어들더니 이를 꼭 악물고는 엎어질 듯 자빠질 듯 논둑으로 횡하게 달아나는 것이다.",
                "어쩌다 동리 어른이,",
                "\"너 얼른 시집을 가야지?\"",
                "하고 웃으면,",
                "\"염려 마서유. 갈 때 되면 어련히 갈라구!\"",
                "이렇게 천연덕스레 받는 점순이었다. 본시 부끄럼을 타는 계집애도 아니거니와 또한 분하다고 눈에 눈물을 보일 얼병이도 아니다.",
                "분하면 차라리 나의 등어리를 바구니로 한번 모질게 후려쌔리고 달아날지언정.",
                "그런데 고약한 그 꼴을 하고 가더니 그 뒤로는 나를 보면 잡아먹으려 기를 복복 쓰는 것이다.",
                "설혹 주는 감자를 안 받아먹는 것이 실례라 하면, 주면 그냥 주었지 '느 집엔 이거 없지.'는 다 뭐냐.",
                "그러잖아도 저희는 마름이고 우리는 그 손에서 배재를 얻어 땅을 부치므로 일상 굽실거린다.",
                "그러면서도 열일곱씩이나 된 것들이 수군수군하고 붙어 다니면 동네의 소문이 사납다고 주의를 시켜준 것도 또 어머니였다.",
                "왜냐하면 내가 점순이 하고 일을 저질렀다가는 점순네가 노할 것이고, 그러면 우리는 땅도 떨어지고 집도 내쫓기고 하지 않으면 안되는 까닭이었다.",
                "그러면서도 아무리 생각하여도 나만 밑지는 노릇이다."
        );

        AtomicInteger lineNo = new AtomicInteger(1);

        lines.forEach(line -> {
            Account account = Account.builder()
                    .name(lineNo.getAndIncrement() + "번째 줄")
                    .balance(1000L)
                    .message(line)
                    // @CreationTimestamp에 의해 updated_at은 자동으로 설정됨
                    .build();
            accountRepository.save(account);
        });

        System.out.println("✅ 전체 텍스트 51줄이 삽입되었습니다.");
    }

}

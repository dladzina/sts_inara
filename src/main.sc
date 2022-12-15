require: slotfilling/slotFilling.sc
  module = sys.zb-common
  
require: patterns.sc
  module = sys.zb-common
  
require: Functions/GetNumbers.js
require: Functions/AccountsSuppliers.js

require: AccountInput.sc 
require: ChangeAccountPerson.sc
require: ChangeAccountPersonCount.sc

require: dicts/MainSuppl.csv
    name = MainSuppl
    var = $MainSuppl

patterns:
    $Yes_for_contacts = (сейчас/*диктуй*/говори*/давай*)
    $No_for_contacts = (самостоятельно/сам/посмотр* сам/найд* сам)
    $Offline = (оффлайн/лично/офлайн/*жив*/offline/ofline/*офис*)
    $Online = (онлайн/*интернет*/online/электрон*)
    $numbers = $regexp<(\d+(-|\/)*)+>
    $mainSuppl = $entity<MainSuppl> || converter = mainSupplConverter
    

init:
    bind("preProcess", function($context) {
        $context.session._lastState = $context.currentState;
        //$context.session._lastState = $context.contextPath ;
    });
    bind("postProcess", function($context) {
        $context.session.lastState = $context.currentState;
    });

    bind("postProcess", function($context) {
        //$context.session._lastState = $context.currentState;
        log("**********" + toPrettyString($context.currentState));
        $context.session.AnswerCnt = $context.session.AnswerCnt || 0;
        $context.session.AnswerCnt += 1;
        
        //$context.session._lastState = $context.contextPath ;
    });

    $global.mainSupplConverter = function($parseTree){
        var id = $parseTree.MainSuppl[0].value;
        return $MainSuppl[id].value;
    }
    

theme: /

    state: Start
        q!: $regex</start>
        script:
            $context.session.AnswerCnt = 0;
        a: Я Инара, ваш виртуальный помощник. Я могу рассказать, как поменять фамилию или количество человек в квитанции, подсказать дату последней оплаты или подсказать контакты поставщика услуг
        random:
            a: Что вы хотите узнать?
            a: По какому вопросу вы обращаетесь?
            a: Задайте Ваш вопрос
            a: Скажите свой вопрос
        script:
            $dialer.bargeInResponse({
                bargeIn: "forced",
                bargeInTrigger: "interim",
                noInterruptTime: 0});
            FindAccountNumberClear();
        # заглушки
        # event: noMatch || onlyThisState = false, toState = "/NoMatch" 
        # intent: /CallTheOperator || onlyThisState = false, toState = "/NoMatch" 
        # intent: /ChangeAccountPerson || onlyThisState = false, toState = "/ChangeAccountPerson/ChangeAccountPerson" 
        # intent: /ChangeAccountPersonCount || onlyThisState = false, toState = "/ChangeAccountPersonCount" 

    state: Hello
        intent!: /привет
        a: Привет привет

    state: NoMatch || noContext = true
        event!: noMatch
        # a: Я не понял. Вы сказали: {{$request.query}}
        script:
            $session.catchAll = $session.catchAll || {};
        
            //Начинаем считать попадания в кэчол с нуля, когда предыдущий стейт не кэчол.
            if ($session.lastState && !$session.lastState.startsWith("/CatchAll")) {
                $session.catchAll.repetition = 0;
            } else{
                $session.catchAll.repetition = $session.catchAll.repetition || 0;
            }
            $session.catchAll.repetition += 1;
        if: $context.session.AnswerCnt == 1
            random:
                a: Извините, я Вас не поняла. Повторите пожалуйста 
                a: Я Вас не поняла. Сформулируйте по-другому 
                a: Скажите еще раз 
                a: Мне плохо слышно, повторите
        else:
            a: Для решения Вашего вопроса перевожу Вас на оператора. Пожалуйста, подождите
            go!: /CallTheOperator
    
    state: SwitchToOperator
        q!: перевод на оператора
        q!: $switchToOperator
        intent!: /CallTheOperator
        if: $context.session.AnswerCnt == 1
            a: Чтобы я переключила Вас на нужного оператора, озвучьте свой вопрос
        else:
            a: Переключаю Вас на оператора. Пожалуйста, подождите
            go!: /CallTheOperator
        

    # перевод на оператора.
    # В А Ж Н О: слова перед переводом говорит стейт, который вызывает этот переход
    state: CallTheOperator
        # a: Перевожу Вас на оператора
        script:
            # Александр Цепелев:
            # Привет. Кто-то делал перевод звонка на оператора с подставлением номера абонента? Как вы в поле FROM передавали этот номер?
            
            # Anatoly Belov:
            # у нас работает так:
            
            # var switchReply = {type:"switch"};
            # switchReply.phoneNumber = "ТУТВНУТРЕННИЙНОМЕР";
            # var callerIdHeader = "\""+$dialer.getCaller()+"\""+" <sip:"+$dialer.getCaller()+"@ТУТВНУТРIP>";
            # switchReply.headers = { "P-Asserted-Identity":  callerIdHeader};
            # $response.replies = $response.replies || [];
            # $response.replies.push(switchReply);
            
            # если звонок передается внутри АТС, т все ок )            
            var switchReply = {type:"switch"};
            switchReply.phoneNumber = "4606"; // номер, на который переключаем
            var callerIdHeader = "\""+ $dialer.getCaller() +"\""+" <sip:"+$dialer.getCaller()+"@92.46.54.218>"; // последнеее - внутренний IP 
            //
            switchReply.headers = { "P-Asserted-Identity":  callerIdHeader, testheader: "header"};
            
            // при true, абонент будет возвращен к диалогу с ботом после разговора с оператором, а также, если оператор недоступен.
            switchReply.continueCall = true; 

            // при true, разговор продолжает записываться, в том числе с оператором и при повторном возвращении абонента в диалог с ботом. Запись звонка будет доступна в логах диалогов.
            switchReply.continueRecording = true; 
            
            $response.replies = $response.replies || [];
            $response.replies.push(switchReply);
            

    state: CallTheOperatorTransferEvent
        event: transfer
        script:
            var status = $dialer.getTransferStatus();
            log(status);
        if: $dialer.getTransferStatus().status === 'FAIL'
            a: Оператор сейчас не может ответить на ваш вопрос. 
        # else:
        #     a: Спасибо, что связались с нами. Оцените, пожалуйста, качество обслуживания.                

    state: repeat || noContext = true
        q!:  * ( повтор* / что / еще раз* / ещё раз*) *
        go!: {{$session.contextPath}}
        # go!: {{ $context.session._lastState }} 
        
    state: bye
        q!: $bye
        intent!: /sys/aimylogic/ru/offerreject || onlyThisState = false, toState = "/Инициация завершения разговора"
        a: Благодарим за обращение!
        random: 
            a: До свидания! 
            a: Надеюсь, я смогла вам помочь. Удачи! 
        script:
            $dialer.hangUp();
            
    state: greeting
        intent: /greeting
        random: 
            a: Пожалуйста || htmlEnabled = false, html = "Пожалуйста"
            a: Не за что || htmlEnabled = false, html = "Не за что"
            a: Я старалась || htmlEnabled = false, html = "Я старалась"
            
    state: looser
        q!: * $looser *
        q!: * $obsceneWord  *
        q!: * $stupid  * 
        random: 
            a: Спасибо. Мне крайне важно ваше мнение || htmlEnabled = false, html = "Спасибо. Мне крайне важно ваше мнение"
            a: Вы очень любезны сегодня || htmlEnabled = false, html = "Вы очень любезны сегодня"
            a: Это комплимент или оскорбление? || htmlEnabled = false, html = "Это комплимент или оскорбление?"
            # здесь хочется Чем я могу Вам помочь? Иначе провисание диалога

    state: HangUp
        event!: hangup
        event!: botHangup
        script: FindAccountNumberClear()

    state: WhereAreYou || noContext = true
        q!: где ты [сейчас]
        a: {{$context.contextPath}}
        #a: {{$context.session._lastState}}            

    state: ClearAccount
        q!: сбрось лицев* 
        script: FindAccountNumberClear();
        #a: Ок

    state: speechNotRecognizedGlobal
        event!: speechNotRecognized
        script:
            $session.speechNotRecognized = $session.speechNotRecognized || {};
            //Начинаем считать попадания в кэчол с нуля, когда предыдущий стейт не кэчол.
            if ($session.lastState && !$session.lastState.startsWith("/speechNotRecognizedGlobal")) {
                $session.speechNotRecognized.repetition = 0;
            } else{
                $session.speechNotRecognized.repetition = $session.catchAll.repetition || 0;
            }
            $session.speechNotRecognized.repetition += 1;
            
        if: $session.speechNotRecognized.repetition >= 3
            a: Кажется, проблемы со связью.
            script:
                $dialer.hangUp();
        else:
        random: 
            a: Извините, я не расслышала. Повторите, пожалуйста.
            a: Не совсем поняла. Можете повторить, пожалуйста?
            a: Повторите, пожалуйста. Вас плохо слышно.

theme: /ИнициацияЗавершения
    
    state: CanIHelpYou 
        a: Нужна ли моя помощь дальше?
        
        state: CanIHelpYouAgree
            q: $yes
            q: $agree
            
            
        state: CanIHelpYouDisagree
            q: $no
            q: $disagree
            go!: /bye

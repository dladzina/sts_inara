require: slotfilling/slotFilling.sc
  module = sys.zb-common
  
require: patterns.sc
  module = sys.zb-common
require: dateTime/dateTime.sc
  module = sys.zb-common  

require: Functions/GetNumbers.js
require: Functions/AccountsSuppliers.js


#########################################
# логирование произошедших ошибок
require: ErrorBind/ErrorLogger.js

#########################################
# ПОДКЛЮЧЕНИЕ ДОПОЛНИТЕЛЬНЫХ СЦЕНАРИЕВ
# сценарий смена собственника
require: ChangeAccountPerson.sc
# сценарий смена количества проживающих
require: ChangeAccountPersonCount.sc
# сценарии по платежам
require: PaymentTotal.sc
# контакты поставщика
require: SupplierContacts.sc
# общие вопросы по алсеко
require: AlsecoCommon.sc
#########################################
# Справочник - основные поставщики
require: dicts/MainSuppl.csv
    name = MainSuppl
    var = $MainSuppl

#########################################
# Общие ответы
require: CommonAnswers.yaml
    var = CommonAnswers

patterns:
    # $Yes_for_contacts = (сейчас/*диктуй*/говори*/давай*)
    # $No_for_contacts = (самостоятельно/сам/посмотр* сам/найд* сам)
    # $Offline = (оффлайн/лично/офлайн/*жив*/offline/ofline/*офис*)
    # $Online = (онлайн/*интернет*/online/электрон*)
    $numbers = $regexp<(\d+(-|\/)*)+>
    $mainSuppl = $entity<MainSuppl> || converter = mainSupplConverter
    $changeOwner = [приобрел* @Недвижимость] *мени* (собственника|хозяина|имя|фамилию)
    

init:
    bind("preProcess", function($context) {
        $context.session._lastState = $context.currentState;
        //$context.session._lastState = $context.contextPath ;
    });
    bind("preMatch", function($context) {
        if($context.request.query){
            $context.request.query = $context.request.query.replaceAll("два нуля","ноль ноль")
            $context.request.query = $context.request.query.replaceAll("два ноля","ноль ноль")
        }
        //$context.request.query += " (клиент авторизован)";
    });
    
    bind("postProcess", function($context) {
        $context.session.lastState = $context.currentState;
        //$context.session._lastState = $context.currentState;
        // log("**********" + toPrettyString($context.currentState));
        $context.session.AnswerCnt = $context.session.AnswerCnt || 0;
        if ((!$context.session.lastState.startsWith("/speechNotRecognizedGlobal"))
            && (!$context.session.lastState.startsWith("/Start/DialogMakeQuestion"))
           )
            $context.session.AnswerCnt += 1;
        
        //$context.session._lastState = $context.contextPath ;
        // добавляю логи всех ответов бота
        $context.session._last_reply = "";
        if ($context.response.replies) {
            var last_reply = "";
            $context.response.replies.forEach(function(reply) {
                if (reply.type === "text") {
                    if (reply.text.match(/\[|\]/g) && reply.text.match(/\(|\)/g)) {
                        last_reply += formatLink(reply.text)
                    } else {
                        last_reply += reply.text
                    }
                }
            });
            $context.session._last_reply =  last_reply;
        }        
    });
    ///ChangeAccountPerson/ChangeAccountPerson
    bind("selectNLUResult", 
    function($context) {
        // log("$context.nluResults"  + toPrettyString( $context.nluResults) );
        // если состояние по "clazz":"/NoMatch" - то оставляем приоритет 
        if (
                ($context.nluResults.intents.length > 0) && 
                ($context.nluResults.intents[0].score > 0.35) && 
                $context.nluResults.intents[0].clazz &&
                ($context.nluResults.intents[0].clazz != "/NoMatch")
            ) {
                // если правило - паттерн и приводит к интенту /SupplierContacts/SupplierContacts, то не меняем
            if (!($context.nluResults.selected.clazz && 
                ($context.nluResults.selected.clazz.startsWith("/SupplierContacts/SupplierContacts")))){
               $context.nluResults.selected = $context.nluResults.intents[0];
            }
            
            # log("$context.nluResults.selected"  + toPrettyString( $context.nluResults.selected) );
            
            return;
        }
        // обработка фразы "да нужна повтори помедленней я записываю
        # log("$context.nluResults "  + toPrettyString( $context) );
        if($context.nluResults.intents.length > 1){
            if (($context.nluResults.intents[0].score < 0.35) && 
                $context.nluResults.intents[0].clazz &&
                ($context.nluResults.intents[0].clazz != "/NoMatch")&&
                ($context.nluResults.intents[1].score > 0.55) && 
                $context.nluResults.intents[1].clazz &&
                ($context.nluResults.intents[1].clazz != "/NoMatch"))
            $context.nluResults.selected = $context.nluResults.intents[1];
            return;
                
        }
        log("$context.nluResults "  + toPrettyString( $context.nluResults) );
        if($context.nluResults.intents.length > 2){
            if (($context.nluResults.intents[0].score < 0.35) && 
                $context.nluResults.intents[0].clazz &&
                ($context.nluResults.intents[0].clazz != "/NoMatch")&&
                ($context.nluResults.intents[2].score > 0.55) && 
                $context.nluResults.intents[2].clazz &&
                ($context.nluResults.intents[2].clazz != "/NoMatch"))
            $context.nluResults.selected = $context.nluResults.intents[2];
            return;
                
        }
        
        
        
        // паттерн TotalPay должен иметь минимальный вес среди всех интентов
        if  ($context.nluResults.selected.clazz == "/PaymentTotal/PaymentQuestion" &&
            $context.nluResults.selected.ruleType == "pattern"){
            if (
                    ($context.nluResults.intents.length > 0) && 
                    # ($context.nluResults.intents[0].score > 0.45) && 
                    $context.nluResults.intents[0].clazz &&
                    ($context.nluResults.intents[0].clazz != "/NoMatch")
                ) {
                $context.nluResults.selected = $context.nluResults.intents[0];
                # log("$context.nluResults.selected TotalPayReplace = "  + toPrettyString( $context.nluResults.selected) );
                
                return;
            }
        }

    }
    );
    # bind("selectNLUResult", function($context) {
    #     // Получим все результаты от всех классификаторов в виде массива.
    #     var allResults = _.chain($context.nluResults)
    #         .omit("selected")
    #         .values()
    #         .flatten()
    #         .value();
    
    #     // Сосчитаем максимальное значение `score` среди всех результатов.
    #     var maxScore = _.chain(allResults)
    #         .pluck("score")
    #         .max()
    #         .value();
    
    #     // Запишем в `nluResults.selected` результат с максимальным весом.
    #     $context.nluResults.selected = _.findWhere(allResults, {
    #         score: maxScore
    #     });
    #     log(toPrettyString($context.nluResults.selected));
    # });
    

    $global.mainSupplConverter = function($parseTree){
        var id = $parseTree.MainSuppl[0].value;
        return $MainSuppl[id].value;
    }
    

theme: /

    state: Start
        q!: $regex</start>
        script:
            $context.session.AnswerCnt = 0;
        # a: Я Инара, ваш виртуальный помощник. Я могу рассказать, как поменять фамилию или количество человек в квитанции, подсказать дату последней оплаты или подсказать контакты поставщика услуг
        a: Я Инара, ваш виртуальный помощник. Я могу рассказать, как поменять фамилию или количество человек в квитанции, 
        a: подсказать дату последней оплаты или контакты поставщика услуг
        script:
            $temp.index = $reactions.random(CommonAnswers.WhatDoYouWant.length);
        a: {{CommonAnswers.WhatDoYouWant[$temp.index]}}
        script:
            if ($dialer.getCaller())
                $analytics.setSessionData("Телефон", $dialer.getCaller());
            $dialer.bargeInResponse({
                bargeIn: "phrase",
                bargeInTrigger: "final",
                noInterruptTime: 0});
             FindAccountNumberClear();
        
        state: DialogMakeQuestion
            intent: /НачалоРазговора
            script:
                $temp.index = $reactions.random(CommonAnswers.WhatDoYouWant.length);
            a: {{CommonAnswers.WhatDoYouWant[$temp.index]}}

    state: Hello
        intent!: /привет
        random:
            a: Здравствуйте!
            a: Алло, я Вас слушаю
        
    
    state: WhatDoYouWant
        script:
            $temp.index = $reactions.random(CommonAnswers.WhatDoYouWant.length);
        a: {{CommonAnswers.WhatDoYouWant[$temp.index]}}

    state: WhatDoYouWantNoContext || noContext = true
        script:
            $temp.index = $reactions.random(CommonAnswers.WhatDoYouWant.length);
        a: {{CommonAnswers.WhatDoYouWant[$temp.index]}}        

    state: OtherTheme
        intent!: /РазноеНаОператора
        intent!:/Квитанция_Дубликат
        intent!:/Квитанция_Доставка
        go!: /NoMatch

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
            script:
                $temp.index = $reactions.random(CommonAnswers.NoMatch.answers.length);
            a: {{CommonAnswers.NoMatch.answers[$temp.index]}}
            # random:
            #     a: Извините, я Вас не поняла. Повторите пожалуйста 
            #     a: Я Вас не поняла. Сформулируйте по-другому 
            #     a: Скажите еще раз 
            #     a: Мне плохо слышно, повторите
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
            # switchReply.phoneNumber = "5015"; // номер, на который переключаем
            switchReply.phoneNumber = "2222"; // номер, на который переключаем
            //switchReply.phoneNumber = "5020"; // номер, на который переключаем
            # switchReply.phoneNumber = "5000"; // номер, на который переключаем
            
            //var callerIdHeader = "\""+ $dialer.getCaller() +"\""+" <sip:"+$dialer.getCaller()+"@10.40.89.112>"; // последнеее - внутренний IP
            var callerIdHeader = "\""+ $dialer.getCaller() +"\""+" <sip:"+$dialer.getCaller()+"@92.46.54.211>"; // последнеее - внутренний IP 
            //
            switchReply.headers = { "P-Asserted-Identity":  callerIdHeader, testheader: "header"};
            
            // при true, абонент будет возвращен к диалогу с ботом после разговора с оператором, а также, если оператор недоступен.
            switchReply.continueCall = false; 

            // при true, разговор продолжает записываться, в том числе с оператором и при повторном возвращении абонента в диалог с ботом. Запись звонка будет доступна в логах диалогов.
            switchReply.continueRecording = false; 
            
            $response.replies = $response.replies || [];
            $response.replies.push(switchReply);
            

    state: CallTheOperatorTransferEvent
        event: transfer
        script:
            var status = $dialer.getTransferStatus();
            log('transfer_status = ' + toPrettyString(status));
        if: $dialer.getTransferStatus().status === 'FAIL'
            a: К сожалению, на данный момент все операторы заняты. Могу ли я Вам еще чем-то помочь? 
        elseif: !$dialer.getTransferStatus().hangup 
            a: Вы вернулись в бота после оператора. 
        #     a: Спасибо, что связались с нами. Оцените, пожалуйста, качество обслуживания.    
        state: CanIHelpYouAgree
            q: $yes
            q: $agree
            intent: /Согласие
            go!: /WhatDoYouWant
            
        state: CanIHelpYouDisagree
            q: $no
            q: $disagree
            intent: /Несогласие
            go!: /bye                

    state: repeat || noContext = true
        q!:  ( повтор* / что / еще раз* / ещё раз*)
        intent!: /Повторить
        if: $session._last_reply
            a: {{$session._last_reply}}
        else:
            go!: {{$session.contextPath}}
    # go!: {{ $context.session._lastState }} 

    state: bye
        q!: $bye
        intent!: /Прощание
        if: $context.session.AnswerCnt == 1
            script:
                $temp.index = $reactions.random(CommonAnswers.WhatDoYouWant.length);
            a: {{CommonAnswers.WhatDoYouWant[$temp.index]}}
        else:        
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
            a: Это моя работа || htmlEnabled = false, html = "Это моя работа"
            a: Я старалась || htmlEnabled = false, html = "Я старалась"
            
    state: looser
        q!: * $looser *
        q!: * $obsceneWord *
        q!: * $stupid  * 
        random: 
            a: Спасибо. Мне крайне важно ваше мнение
            a: Вы очень любезны сегодня
            a: Это комплимент или оскорбление?
        script:
            $analytics.setMessageLabel("Отрицательная")
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
                $session.speechNotRecognized.repetition = $session.speechNotRecognized.repetition || 0;
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
                a: Повторите, пожалуйста. Вас не слышно.

    state: sessionDataSoftLimitExceeded
        # // обрабатываем событие о достижении soft лимита
        event!: sessionDataSoftLimitExceeded
        script:
            SendWarningMessage('Достигнут лимит sessionDataSoftLimitExceeded')

theme: /ИнициацияЗавершения
    
    state: CanIHelpYou 
        script:
            $temp.index = $reactions.random(CommonAnswers.CanIHelpYou.length);
        a: {{CommonAnswers.CanIHelpYou[$temp.index]}}
        
        state: CanIHelpYouAgree
            q: $yes
            q: $agree
            intent: /Согласие
            intent: /Согласие_помочь
            go!: /WhatDoYouWant
            
        state: CanIHelpYouDisagree
            q: $no
            q: $disagree
            intent: /Несогласие
            intent: /Несогласие_помочь
            go!: /bye


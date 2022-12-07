theme: /
# **********************************************************
# сценарий по вводу номера лицевого счета
# **********************************************************
# зависимости от других модулей:
# 1.  общие паттерны - да, нет, согласие, отказ
# require: patterns.sc
#     module = sys.zb-common
# 2. Настройки в модуле chatbot.yaml -  Сколько раз уточняем по номеру ЛС
# injector:
#   AccountInputSettings:
#     MaxRetryCount: 2 
# 3. Настроен обработчик сохранения состояния - в главном модуле 
    # bind("preProcess", function($context) {
    #     $context.session._lastState = $context.currentState;
    #     //$context.session._lastState = $context.contextPath ;
    # });
# 4. Подключен файл AccountNumberInput.js  - запросы к Сервису, информация по ЛС
# 5. Подключен файл GetNumbers.js - вычленяет номер ЛС из найденных сущностей
# require: Functions/GetNumbers.js    
# 6. Добавить интент "подождите", "ГдеНомерЛС" в CAILA

# подключение модуля: 
#    require: AccountInput.sc
#    require: Functions/GetNumbers.js

# !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
# Важно - в телефонном сценарии добавить очистку номера ЛС после окончания сесии (завершили разговор)
# Почему важно - стейт модальный, просто так из него не выйдешь
    # state: HangUp
    #     event!:hangup
    #     event: botHangup
    #     script: FindAccountNumberClear()

# **********************************************************
# Пример использования в стейте:
# ----------------------------------------------------------
#     state: NewAccountMainInfo
#         q: лицев* *
#         if: ($session.Account && $session.Account.Number > 0)
#             a: сейчас дам вам еще информацию по счёту {{$session.Account.Number}}
#             script: 
#                 $reactions.answer(GetAccountNumAnswer($session.Account.Number));
#         elseif: ($session.Account && $session.Account.Number < 0)
#             a: что ж с тобой делать? нет у тебя лицевого счёта ... 
#         else: 
#             a: давайте уточним ваш номер счёта
#             go!:/AccountNumInput/AccountInput
# **********************************************************



require: Functions/AccountNumberInput.js



theme: /AccountNumInput

    state: AccountInput || modal = true
        script: 
            log( toPrettyString($context.session._lastState) );
            if (($context.session._lastState.substr(1,15)) != "AccountNumInput")
            {
                $session.oldState = $context.session._lastState;  
                FindAccountNumberStart();
            }
            $session.Account.MaxRetryCount = $injector.AccountInputSettings.MaxRetryCount || 3;
            $session.Account.RetryAccount = $session.Account.RetryAccount || 0;
            $session.Account.RetryAccount++;
        if: $session.Account.RetryAccount <= $session.Account.MaxRetryCount
            a: Назовите номер вашего лицевого счета. Пожалуйста, оставайтесь на линии, пока я буду искать его
        else: // уже запрашивали номер ЛС больше 2-х раз. Зафиксировать результат - не смогла Вас понять и вернуть управление в исходный стейт со всеми данными
            script: FindAccountNumberSetResult("DontUnderstand");
            #a: Возвращаюсь назад в {{toPrettyString($session.oldState)}}
            go!: {{ $session.oldState }}
        
        state: AccountInputWait    
            intent: /подождите
            a: да, жду Вас
            
        state: AccountInputWhereIsAccount
            intent: /ГдеНомерЛС
            a: Номер отображается в счёте Алсеко сразу **над таблицей**.  Вы можете посмотреть счёт и назвать номер **сейчас**?
            state: AccountInputWhereIsAccountYes
                q: $yes
                q: $agree
                script:  
                    $session.Account.RetryAccount--;
                # a: Ваш лицевой счет {{$session.AccountNumber}}. {{ $session.oldState }}
                go!: ../..
            
            state: AccountInputWhereIsAccountDecline 
                q: $no
                q: $disagree
                script: 
                    FindAccountNumberSetResult("DontKnow"); 
                go!: {{ $session.oldState }}
        
        state: AccountInputNumber 
            
            # проверяем наличие цифр в запросе. если есть, значит говорит номер лицевого счета
            q: * $numbers *
            q: * @duckling.number *
            go!: FindAccount

            state: FindAccount
                script: 
                    TrySetNumber(words_to_number($entities));

                    FindAccountAddress().then(function(res){
                        log(toPrettyString(res));
                        if (res && res.count > 0){
                            log(res.data[0].address_full_name);
                            $session.Account.Address = res.data[0].address_full_name;
                            $reactions.transition('../AccountAddressConfirm')
                        }else {
                            $session.Account.Address = "";
                            $reactions.transition('../AccountNotFound');
                        }
                    }).catch(function err() {
                        $reactions.answer("Что-то сервер барахлит. ");
                        $reactions.transition('../AccountNotFound')
                    });
                        

            state: AccountAddressConfirm
                a: Ваш адрес {{$session.Account.Address}}. Верно? 

                state: AccountAddressConfirmYes
                    q: $yes
                    q: $agree
                    script:  
                        FindAccountNumberSetSuccees("Address");
                    # a: Ваш лицевой счет {{$session.AccountNumber}}. {{ $session.oldState }}
                    go!: {{ $session.oldState }}
                
                state: AccountAddressDecline 
                    q: $no
                    q: $disagree
                    script: 
                        FindAccountNumberSetResult("AddressCancel"); 
                    if: $session.Account.RetryAccount < $session.Account.MaxRetryCount
                        a: Давайте еще раз проверим
                    go!: /AccountNumInput/AccountInput
                
                state: AccountAddressNoMatch
                     event: noMatch || noContext = true
                     a: Извините, я Вас не расслышала.
                     go!: ..

            state: AccountNotFound
                a: Извините, я не нашла Ваш лицевой счёт. 
                if: $session.Account.RetryAccount < $session.Account.MaxRetryCount
                    a: Давайте еще раз проверим
                go!: /AccountNumInput/AccountInput

        state: AccountInputNoNumber
            event: noMatch || noContext = true
            a: Это не похоже на номер лицевого счета. Попробуйте еще раз.
            go!: ..
    
    state: DontKnow
        intent: /DontKnow || fromState = "/AccountNumInput/AccountInput"
        script:FindAccountNumberSetResult("DontKnow"); 
        # a: Возвращаю управление в стейт {{$session.oldState}}
        go!: {{$session.oldState}}    
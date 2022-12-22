require: Functions/AccountPay.js

theme: /PaymentTotal

    # скрипт - вовзращает дату последеней оплаты по лицевому счету
    state: GetDateLastPay
        intent!: /GetDateLastPay

        # запрашивает информацию с БД по дате последней оплаты 
        # если ЛС нет, то отрабатывает тоже: запрашивает ЛС. 
        AccountPayDateMessage:
            needCleanEmptyAccount = false
            okState = /PaymentTotal/GetDateLastPay/CanIHelpYou
            errorState = /PaymentTotal/GetDateLastPay/CanIHelpYou 
            # errorState = /CallTheOperator
            noAccountState = /PaymentTotal/GetDateLastPay/CanIHelpYou
        
        state: CanIHelpYou
            # a: Нужна ли моя помощь дальше?
            script:
                $temp.index = $reactions.random(CommonAnswers.CanIHelpYou.length);
            a: {{CommonAnswers.CanIHelpYou[$temp.index]}}
            
            state: CanIHelpYouAgree
                q: $yes
                q: $agree
                go!: /WhatDoYouWant
                
            state: CanIHelpYouDisagree
                q: $no
                q: $disagree
                go!: /bye        
    
    
    state: GetDateLastPayAnswer
        # смотрим, был ли лицевой счет выявлен в ходе диалога
        if: ($session.Account && $session.Account.Number > 0)
            # Есть номер лицевого счета, будем давать информацию по нему по контактам поставщиков
            go!: GetDateLastPayInfo
            # a: сейчас дам вам еще информацию по счёту {{$session.Account.Number}}
            # script: 
            #      $reactions.answer(GetAccountNumAnswer($session.Account.Number));
        elseif: ($session.Account && $session.Account.Number < 0)
            # a: что ж с тобой делать? нет у тебя лицевого счёта ... 
            go!: SendToOperator
        else: 
            # здесь идет определение, что ЛС в рамках дилагога еще не запрашивался - передаем управление туда
            a: Давайте уточним Ваш лицевой счет
            go!:/AccountNumInput/AccountInput      
            
        state: SendToOperator
            a: Для решения вашего вопроса перевожу Вас на оператора.
            go!: /CallTheOperator
            
        state: GetDateLastPayInfo
            if: GetAccountPayShortInfo()
                a: {{GetPaymentAnswer()}} 
            else: 
                a: К сожалению, мне не удалось получить информацию по оплате. 
            go!: ../SendToOperator

    state: PaymentQuestion
        intent!: /Платеж
        a: Давайте посмотрим Ваши платежи
        go!: PaymentQuestionAnswerDateLastPay
        
        state: PaymentQuestionAnswerDateLastPay
            script:
                if ($session.Account && $session.Account.Number < 0) FindAccountNumberClear();
            go!: /PaymentTotal/GetDateLastPayAnswer
            

        
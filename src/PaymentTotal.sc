require: Functions/AccountPay.js

patterns:
    $TotalPay = (о/за)(плат*|плач*) (платеж/сумм*/квитанц*)

theme: /PaymentTotal

    # скрипт - вовзращает дату последеней оплаты по лицевому счету
    state: GetDateLastPay
        intent!: /GetDateLastPay

        # запрашивает информацию с БД по дате последней оплаты 
        # если ЛС нет, то отрабатывает тоже: запрашивает ЛС. 
        AccountPayDateMessage:
            needCleanEmptyAccount = true
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
    

    state: PaymentQuestion
        intent!: /Платеж
        q!: * $TotalPay *
        a: Давайте посмотрим Ваши платежи, а потом я переведу Вас на оператора
        AccountPayDateMessage:
            needCleanEmptyAccount = true
            okState = SendToOperator
            errorState = SendToOperator
            noAccountState = SendToOperator
        
        state: SendToOperator
            a: Для решения вашего вопроса перевожу Вас на оператора.
            go!: /CallTheOperator
        


        
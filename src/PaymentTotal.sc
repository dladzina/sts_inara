require: Functions/AccountPay.js

theme: /PaymentTotal

    # скрипт - вовзращает дату последеней оплаты по лицевому счету
    state: GetDateLastPay
        intent!: /GetDateLastPay
    
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
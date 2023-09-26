theme: /SecondPayment
    
    state: SecondPayment
        intent!: /SecondPayment
        a: Вы ошибочно оплатили счет дважды? Правильно?
        
        state: AnotherQuestion
            intent: /AnotherQuestion
            go!: /WhatDoYouWant        
            
            
        state: Agreement
            intent: /Согласие
            a: Вы провели оплату сегодня?
            
            state: Согласие_сегодня
                intent: /Согласие_сегодня
                a: Вы провели оплату сегодня?
                
            state: Несогласие_не_знаю
                intent: /Несогласие_сегодня
                intent: /Не_знаю
                a: Ваши деньги уже поступили поставщикам и  будут учтены как переплата  в следующей квитанции
                
                
                
        
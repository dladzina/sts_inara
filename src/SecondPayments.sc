theme: /SecondPayment
    
    state: SecondPayment
        intent!: /SecondPayment
        random: 
            a: Вы ошибочно оплатили счет дважды? Правильно?
            a: Вы оплатили два раза? Правильно?
        
        state: AnotherQuestion
            intent: /NoMatch
            a: state: AnotherQuestion
            go!: /WhatDoYouWant        
            
            
        state: Agreement
            intent: /Согласие
            random: 
                a: Вы провели оплату сегодня?
                a: Вы оплатили сегодня?
                a: Когда Вы оплатили? Сегодня?
            
            state: Согласие_сегодня
                intent: /Согласие
                intent: /Согласие_сегодня
                a: Сегодня до 16:00 нужно скрин удостоверения оплачивающего и чек по адресу Карасай Батыра 155 в офис Алсеко
                go!: /SecondPayment/SecondPayment/Agreement/Согласие_сегодня/Question
                
                state: Question
                    a: Могу ли я вам чем-то еще помочь?
                
            state: Несогласие_не_знаю
                intent: /Несогласие
                intent: /Не_знаю
                a: Ваши деньги уже поступили поставщикам и  будут учтены как переплата  в следующей квитанции
                a: АЛСЕКО не принимает деньги за услуги. Эти деньги поступают от банка напрямую поставщикам услуг.
                a: Возврат могут сделать только поставщики, контакты которых указаны в квитанции
                go!: /SecondPayment/SecondPayment/Agreement/Несогласие_не_знаю/CanIHelpYou
                
                    
                state: Да_локальное
                    intent: /Согласие
                    intent: /Согласие_помочь
                    a: Скажите Ваш вопрос?
                    
                state: Whobringsdocs
                    intent: /Whobringsdocs
                    intent: /собственник_привозит_документы
                    intent: /Онлайн
                    intent: /Time_to_get_money
                    a: У каждого поставщика услуг свои правила. Поэтому уточните у них
                    go!: /SecondPayment/SecondPayment/Agreement/Несогласие_не_знаю/CanIHelpYou
                    
                state: HowFindContacts
                    intent: /HowFindContacts
                    intent: /WhereToGo
                    a: Вам нужно обратиться с заявлением к каждому поставщику услуг. 
                    a: Контакты поставщиков Вы можете найти в квитанции, либо на нашем сайте alseco.kz
                    go!: /SecondPayment/SecondPayment/Agreement/Несогласие_не_знаю/CanIHelpYou
                
                state: WhereToGo
                    intent: /WhereToGo
                    a: Контакты поставщиков Вы можете найти в квитанции, либо на нашем сайте alseco.kz
                    go!: /SecondPayment/SecondPayment/Agreement/Несогласие_не_знаю/CanIHelpYou
                    
                state: ComeToAlseco
                    intent: /ComeToAlseco
                    a: К нам нет. Возвраты осуществляют только поставщики услуг
                    go!: /SecondPayment/SecondPayment/Agreement/Несогласие_не_знаю/CanIHelpYou
                    
                    
                state: CanIHelpYou ||noContext = false
                    # CommonAnswers
                    script:
                        $temp.index = $reactions.random(CommonAnswers.CanIHelpYou.length);
                    a: {{CommonAnswers.CanIHelpYou[$temp.index]}}
                    # a: Нужна ли моя помощь дальше?
                    
                    state: Repeat
                        intent: /Согласие_повторить
                        intent: /Повторить
                        go!: ../../SupplierContactsSayContacts
        
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
                        
                
                
        
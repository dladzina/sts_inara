theme: /AlsecoCommon
# адрес Алсеко
    state: AlsecoAddress
        intent!:/AlsecoGiveAddress
        random:
            a: Наш адрес - Карасай батыра 155, угол Виноградова. Возможно, я или оператор сможем Вам помочь по телефону? 
            a: Карасай батыра, 155, угол Виноградова. Может, мы можем решить вопрос по телефону?
        go!: /WhatDoYouWantNoContext
        script:
            $dialer.bargeInResponse({
                bargeIn: "forced",
                bargeInTrigger: "final",
                noInterruptTime: 0});

        state: AlsecoAddressRepeat
            intent: /AlsecoAdressConfirm
            intent: /Повторить
            go!: ..


# телефоны алсеко
# на этот вопрос не отвечаем
    state: AlsecoPhones
        intent!:/AlsecoGivePhones
        go!: /NoMatch


    state: AlsecoMobApp
        intent!: /AlsecoGiveMobApp
        random:
            a: наше приложение Алсеко есть в гугл плей и ап сторе 

    state: AlsecoSite
        intent!:/AlsecoGiveSite
        random:
            a: в интернете вы нас можете найти на сайте алсеко точка кей зет 
 
    state: AlsecoEmail
        intent!:/AlsecoGiveEmail
        random:
            a: наш емейл - info собачка алсеко кей зет.буква Ка в Алсеко пишется через букву си - или эс на русском. 
 
    state: AlsecoWorkingDays
        intent!:/AlsecoGiveWorkingDays
        random:
            a: мы работаем в будни с 8 до 16, звонки принимаем до 18 00. суббота, воскресенье - выходной

    state: AlsecoPartnersSaleContacts
        intent!:/AlsecoGivePartnersSaleContacts
        random:
            a: По поводу партнерства с нами вы можете позвонить по телефону +7 701 485 79 86
        # if: countRepeatsInRow(true) < 3
        if:    countRepeats() < 3
            a: Повторить?
        else: 
            go!:../CanIHelpYou            

        state: SaleContactsRepeat:
            intent: /Повторить
            intent: /Согласие_продиктовать_список_поставщиков
            intent: /Согласие_повторить
            intent: /Повторить
            q: $numbersByWords 
            a: +7 701 485 79 86
            if: countRepeatsInRow() < 3
            # if:    countRepeats() < 3
                a: Повторить?
            else: 
                go!:../../CanIHelpYou  
            # go!: ..        

        state: SaleContactsDecline
            intent: /Несогласие
            intent: /Несогласие_повторить
            go!:../../CanIHelpYou            

    
    state: AlsecoFinance
        intent!:/AlsecoGiveFinance
        random:
            a: Перевожу Ваш звон+ок на опер+атора
            go!: /CallTheOperator

    state: CanIHelpYou ||noContext = false
        # CommonAnswers
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

        # random:
        #     a: Мы находимся по адресу - -  Карасай Батыра, 155
        #     a: Наш +адрес - - Карас+ай Бат+ыра **155**, угол Виногр+адова

theme: /AlsecoCommon
    state: AlsecoAddress
        intent!:/AlsecoGiveAddress
        random:
            a: Наш адрес - Карасай батыра 155, угол Виноградова. Возможно, я или оператор сможем Вам помочь по телефону? Задайте Ваш вопрос
            a: Карасай батыра, 155 угол Виноградова. Может, мы можем решить вопрос по телефону?

    state: AlsecoPhones
        intent!:/AlsecoGivePhones
        go!: /NoMatch


    state: AlsecoMobApp
        intent!: /AlsecoGiveMobApp
        random:
            a: наше приложение Алсеко есть в гугл плей и апп сторе 

    state: AlsecoSite
        intent!:/AlsecoGiveSite
        random:
            a: в интернете вы нас можете найти на сайте алсеко кей зет 
 
    state: AlsecoEmail
        intent!:/AlsecoGiveEmail
        random:
            a: наш емейл - info собачка алсеко кей зет.буква к в Алсеко пишется через букву си - или эс на русском. 
 
    state: AlsecoWorkingDays
        intent!:/AlsecoGiveWorkingDays
        random:
            a: мы работаем в будни с 8 до 16, звонки принимаем до 18 00. суббота, воскресенье - выходной

    state: AlsecoPartnersSaleContacts
        intent!:/AlsecoGivePartnersSaleContacts
        random:
            a: мы работаем в будни с 8 до 16, звонки принимаем до 18 00. суббота, воскресенье - выходной
    
    state: AlsecoFinance
        intent!:/AlsecoGiveFinance
        random:
            a: Перевожу Ваш звон+ок на опер+атора
            go!: /CallTheOperator
        # random:
        #     a: Мы находимся по адресу - -  Карасай Батыра, 155
        #     a: Наш +адрес - - Карас+ай Бат+ыра **155**, угол Виногр+адова

theme: /PersonChange
    
    state: PersonChange
        intent!: /PersonChange
        intent!: /ChangeAccountDetails
        a: Сменить реквизиты можно в офисе или онлайн. Вы хотите подать заявку онлайн?
        

        state: Offline
            #intent: /Offline
            q: * $Offline *
            q: $no 
            q: $disagree 
            a: Вы можете обратиться в абонентский отдел любого из поставщиков услуг, указанных в верхней части счёта на оплату.
            #go!: /AfterPersonalAccount
            go!: /PersonChange/PersonChange/Offline/AfterPersonalAccount
            
            state: AfterPersonalAccount
                a: Хотите узнать, к каким поставщикам можно обратиться?
    
                state: NoCurrent
                    q: $no
                    q: $disagree
                    a: Перечислить необходимые документы?
                    
                    state: NoCurrent
                        q: $no
                        q: $disagree
                        # a:  Интент "Инициация завершения диалога"
                        go!: /ИнициацияЗавершения/CanIHelpYou
                        
                    state: YesCurrent
                        q: $yes
                        q: $agree
                        go!: /PersonChange/PersonChange/DocumentsForLandlords/YesCurrent
                                
                state: YesCurrent
                    q: $yes
                    q: $agree
                    # смотрим, был ли лицевой счет выявлен в ходе диалога
                    if: ($session.Account && $session.Account.Number > 0)
                        go!: SupplierContactsByAccount
                        # a: сейчас дам вам еще информацию по счёту {{$session.Account.Number}}
                        script: 
                             $reactions.answer(GetAccountNumAnswer($session.Account.Number));
                    elseif: ($session.Account && $session.Account.Number < 0)
                        # a: что ж с тобой делать? нет у тебя лицевого счёта ... 
                        go!: SupplierContactsFull
                    else: 
                        a: Чтобы я дала контакты нужных Вам поставщиков, нужен Ваш лицевой счёт
                        go!:/AccountNumInput/AccountInput                    
                    
                    # a: Чтобы я дала Вам контакты нужных Вам поставщиков, нужен Ваш лицевой счёт
                    # a:   Мы в блоке определения ЛС
                    # a:  ЛС определился? да или Нет?
                    
                    state: SupplierContactsFull
                        q: $no
                        q: $disagree
                        a:   ЛС не определен
                        a:   Вы можете обратиться  к одному из поставщиков коммунальных услуг на выбор -  АлматыЭнергоСбыт, Алматинские тепловые сети, а р це Алматыгаз,  Тартып или Алматы Су.
                        a:  Перечислить необходимые документы?
                        go!: /PersonChange/PersonChange/DocumentsForLandlords
                                    
                    state: SupplierContactsByAccount
                        q: $yes
                        q: $agree
                        a:   ЛС определен
                        a:   Вы можете обратиться  к одному из поставщиков коммунальных услуг на выбор -  АлматыЭнергоСбыт, Алматинские тепловые сети, а р це Алматыгаз,  Тартып или Алматы Су.
                        a:  Перечислить необходимые документы?
                        go!: /PersonChange/PersonChange/DocumentsForLandlords
                        
                    state: Contacts
                        a:   Контакты можно узнать в приложении алсеко. Мне продиктовать Вам телефоны сейчас?
                        
                        state: NoCurrent
                            q: $no
                            q: $disagree
                            go!: /PersonChange/PersonChange/DocumentsForLandlords
                                    
                        state: YesCurrent
                            q: $yes
                            q: $agree
                            a:   Записывайте. --- АлматыЭнергоСбыт - телефон 356, 99, 99. Алматинские тепловые сети, 341, 0, 777.  АлматыСу,  3, 777, 444. Алматыгаз,  244, 55,  33. Тартып - 393, 08, 03.
                            go!: /PersonChange/PersonChange/DocumentsForLandlords
        state: Online
            #intent: /Online
            q: * $Online *
            q: $yes
            q: $agree
            a: Это можно сделать на сайте смарт точка алсеко точка кей зет.
            a: Зайдите в личный кабинет через э це пэ собственника жилья. Выберите раздел Мои Заявки.
            a: Там создайте новую заявку, укажите Алсеко как поставщика услуг и выберите заявку.
            a: Дальше следуйте инструкции
            # a: Интент "Инициация завершения диалога"
            go!: /ИнициацияЗавершения/CanIHelpYou
                
        state: DocumentsForLandlords
            
            state: NoCurrent
                q: $no
                q: $disagree
                # a:  Интент "Инициация завершения диалога"
                go!: /ИнициацияЗавершения/CanIHelpYou                                
                
            state: YesCurrent
                q: $yes
                q: $agree
                a:  Необходимые документы: заявление, и правоустанавливающими документами на объект недвижимости. Хотите узнать какие документы на собственность подходят?
            
                state: NoCurrent
                    q: $no
                    q: $disagree
                    # a:  Интент "Инициация завершения диалога"
                    go!: /ИнициацияЗавершения/CanIHelpYou
                                    
                state: YesCurrent
                    q: $yes
                    q: $agree
                    a:  Подходят --копии договора купли-продажи, -дарения, -справка о наличии недвижимого имущества, или  зарегистрированных правах на недвижимое имущество с портала е гов
                    # a:  Интент "Инициация завершения диалога"
                    go!: /ИнициацияЗавершения/CanIHelpYou
        
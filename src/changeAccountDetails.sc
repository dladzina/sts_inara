theme: /PersonChange
    
    state: PersonChange
        intent!: /PersonChange
        a: Сменить реквизиты можно в офисе или онлайн. Вы хотите подать заявку онлайн?
        

        state: Offline
            #intent: /Offline
            q: * $Offline *
            q: * $No *
            a: Вы можете обратиться в абонентский отдел любого из поставщиков услуг, указанных в верхней части счёта на оплату.
            #go!: /AfterPersonalAccount
            go!: /PersonChange/PersonChange/Offline/AfterPersonalAccount
            
            state: AfterPersonalAccount
                a: Хотите узнать, к каким поставщикам можно обратиться?
    
                state: NoCurrent
                    q: * $No *
                    a: Перечислить необходимые документы?
                    
                    state: NoCurrent
                        q: * $No *
                        a:  Интент "Инициация завершения диалога"
                        
                    state: YesCurrent
                        q: * $Yes *
                        go!: /PersonChange/PersonChange/DocumentsForLandlords/YesCurrent
                                
                state: YesCurrent
                    q: * $Yes *
                    a:   Чтобы я дала Вам контакты нужных Вам поставщиков, нужен Ваш лицевой счёт
                    a:   Мы в блоке определения ЛС
                    a:  ЛС определился? да или Нет?
                    
                    state: NoCurrent
                        q: * $No *
                        a:   ЛС не определен
                        a:   Вы можете обратиться  к одному из поставщиков коммунальных услуг на выбор -  АлматыЭнергоСбыт, Алматинские тепловые сети, а р це Алматыгаз,  Тартып или Алматы Су.
                        a:  Перечислить необходимые документы?
                        go!: /PersonChange/PersonChange/DocumentsForLandlords
                                    
                    state: YesCurrent
                        q: * $Yes *
                        a:   ЛС определен
                        a:   Вы можете обратиться  к одному из поставщиков коммунальных услуг на выбор -  АлматыЭнергоСбыт, Алматинские тепловые сети, а р це Алматыгаз,  Тартып или Алматы Су.
                        a:  Перечислить необходимые документы?
                        go!: /PersonChange/PersonChange/DocumentsForLandlords
                        
                    state: Contacts
                        a:   Контакты можно узнать в приложении алсеко. Мне продиктовать Вам телефоны сейчас?
                        
                        state: NoCurrent
                            q: * $No *
                            go!: /PersonChange/PersonChange/DocumentsForLandlords
                                    
                        state: YesCurrent
                            q: * $Yes *
                            a:   Записывайте. --- АлматыЭнергоСбыт - телефон 356, 99, 99. Алматинские тепловые сети, 341, 0, 777.  АлматыСу,  3, 777, 444. Алматыгаз,  244, 55,  33. Тартып - 393, 08, 03.
                            go!: /PersonChange/PersonChange/DocumentsForLandlords
        state: Online
            #intent: /Online
            q: * $Online *
            q: * $Yes *
            a: Это можно сделать на сайте смарт точка алсеко точка кей зет.
            a: Зайдите в личный кабинет через э це пэ собственника жилья. Выберите раздел Мои Заявки.
            a: Там создайте новую заявку, укажите Алсеко как поставщика услуг и выберите заявку.
            a: Дальше следуйте инструкции
            a: Интент "Инициация завершения диалога"
                
        state: DocumentsForLandlords
            
            state: NoCurrent
                q: * $No *
                a:  Интент "Инициация завершения диалога"
                                
            state: YesCurrent
                q: * $Yes *
                a:  Необходимые документы: заявление, и правоустанавливающими документами на объект недвижимости. Хотите узнать какие документы на собственность подходят?
            
                state: NoCurrent
                    q: * $No *
                    a:  Интент "Инициация завершения диалога"
                                    
                state: YesCurrent
                    q: * $Yes *
                    a:  Подходят --копии договора купли-продажи, -дарения, -справка о наличии недвижимого имущества, или  зарегистрированных правах на недвижимое имущество с портала е гов
                    a:  Интент "Инициация завершения диалога"
        
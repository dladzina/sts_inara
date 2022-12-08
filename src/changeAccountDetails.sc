theme: /PersonChange
    
    state: PersonChange
        intent!: /ChangeAccountDetails
        a: Сменить реквизиты можно в офисе или онлайн. Вы хотите подать заявку онлайн?
        

        state: Offline
            #intent: /Offline
            q: * $Offline *
            q: $no 
            q: $disagree 
            a: Вы можете обратиться в абонентский отдел любого из поставщиков услуг, указанных в верхней части счёта на оплату.
            #go!: /AfterPersonalAccount
            go!: /PersonChange/PersonChange/Offline/Suppliers_List_Info
            
            state: Suppliers_List_Info
                a: Хотите узнать, к каким поставщикам можно обратиться?
                script:
                    if ($session.Account && $session.Account.Number < 0) FindAccountNumberClear();
                
    
                state: No_Suppliers_List
                    q: $no
                    q: $disagree
                    go!: /PersonChange/PersonChange/DocumentsForLandlords
                                
                state: Yes_Suppliers_List
                    # пользователь сказал, что хочет узнать контакты поставщиков 
                    #  уточняем, есть ли ЛС. Если нет, то даем контакты всех
                    # если говорит номер ЛС, то даем только тех, что есть в квитанции
                    q: $yes
                    q: $agree
                    # смотрим, был ли лицевой счет выявлен в ходе диалога
                    if: ($session.Account && $session.Account.Number > 0)
                        # Есть номер лицевого счета, будем давать информацию по нему по контактам поставщиков
                        go!: SupplierContactsByAccount
                        # a: сейчас дам вам еще информацию по счёту {{$session.Account.Number}}
                        # script: 
                        #      $reactions.answer(GetAccountNumAnswer($session.Account.Number));
                    elseif: ($session.Account && $session.Account.Number < 0)
                        # a: что ж с тобой делать? нет у тебя лицевого счёта ... 
                        go!: SupplierContactsFull
                    else: 
                        # здесь идет определение, что ЛС в рамках дилагога еще не запрашивался - передаем управление туда
                        a: Чтобы я дала контакты нужных Вам поставщиков, нужен Ваш лицевой счёт
                        go!:/AccountNumInput/AccountInput                    
                    

                    state: SupplierContactsFull
                        # a:   ЛС не определен
                        a:   Вы можете обратиться  к одному из поставщиков коммунальных услуг на выбор -  АлматыЭнергоСбыт, Алматинские тепловые сети, а р це Алматыгаз,  Тартып или Алматы Су.
                        go!: /PersonChange/PersonChange/DocumentsForLandlords
                                    
                    state: SupplierContactsByAccount
                        # где-то здесь надо получить список поставщиков из БД и сформировать строку 
                        a:   ЛС определен
                        a:   Вы можете обратиться  к одному из поставщиков коммунальных услуг на выбор -  АлматыЭнергоСбыт, Алматинские тепловые сети, а р це Алматыгаз,  Тартып или Алматы Су.
                        # go!: /PersonChange/PersonChange/Offline/Yes_Suppliers_List/Contacts
                        go!: ../Contacts
                        
                    state: Contacts
                        a:   Контакты можно узнать в приложении алсеко. Мне продиктовать Вам телефоны сейчас?
                        
                        state: No_Contacts
                            q: $no
                            q: $disagree
                            go!: /PersonChange/PersonChange/DocumentsForLandlords
                                    
                        state: Yes_Contacts
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
            a:  Перечислить необходимые документы?
            
            state: No_List_Doc
                q: $no
                q: $disagree
                # a:  Интент "Инициация завершения диалога"
                go!: /ИнициацияЗавершения/CanIHelpYou                                
                
            state: Yes_List_Doc
                q: $yes
                q: $agree
                a:  Необходимые документы: заявление, и правоустанавливающими документами на объект недвижимости. Хотите узнать какие документы на собственность подходят?
            
                state: No_Property_Documents
                    q: $no
                    q: $disagree
                    # a:  Интент "Инициация завершения диалога"
                    go!: /ИнициацияЗавершения/CanIHelpYou
                                    
                state: Yes_Property_Documents
                    q: $yes
                    q: $agree
                    a:  Подходят --копии договора купли-продажи, -дарения, -справка о наличии недвижимого имущества, или  зарегистрированных правах на недвижимое имущество с портала е гов
                    # a:  Интент "Инициация завершения диалога"
                    go!: /ИнициацияЗавершения/CanIHelpYou
        
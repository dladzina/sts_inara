theme: /ChangeAccountPersonCount
    
    state: ChangeAccountPersonCount
        intent!: /ChangeAccountPersonCount
        a: Изменить количество проживающих реквизиты можно в офисе или онлайн. Вы хотите подать заявку онлайн?
        
        state: ToTheOperator
            event: noMatch
            a: Перевод на оператора!
        
        state: Offline
            #intent: /Offline
            q: * $Offline *
            q: $no 
            q: $disagree 
            event: speechNotRecognized
            a: Вы можете обратиться в абонентский отдел любого из поставщиков услуг, указанных в верхней части счёта на оплату, или в Алсеко по адресу Карасай Батыра, 155. Хотите узнать, к каким поставщикам можно обратиться?

            state: No_Suppliers_List
                q: $no
                q: $disagree
                event: noMatch
                go!: /ChangeAccountPersonCount/ChangeAccountPersonCount/DocumentsToChangePersonCount
                    
            state: Yes_Suppliers_List
                # пользователь сказал, что хочет узнать контакты поставщиков 
                #  уточняем, есть ли ЛС. Если нет, то даем контакты всех
                # если говорит номер ЛС, то даем только тех, что есть в квитанции
                q: $yes
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
                    a:   Вы можете обратиться  к одному из поставщиков коммунальных услуг на выбор -  АлматыЭнергоСбыт, Алматинские тепловые сети, а р це Алматыгаз,  Тартып или Алматы Су.
                    go!: ../Contacts
                                    
                state: SupplierContactsByAccount
                    # где-то здесь надо получить список поставщиков из БД и сформировать строку 
                    a:   ЛС определен
                    a:   Вы можете обратиться  к одному из поставщиков коммунальных услуг на выбор -  АлматыЭнергоСбыт, Алматинские тепловые сети, а р це Алматыгаз,  Тартып или Алматы Су.
                    # go!: /PersonChange/PersonChange/Offline/Yes_Suppliers_List/Contacts
                    go!: ../Contacts
                        
                state: Contacts
                    a:   Контакты можно узнать в мобильном приложении алсеко или я могу продиктовать Вам телефоны сейчас?
                        
                    state: No_Contacts
                        q: $no
                        q: $disagree
                        q: $No_for_contacts
                        go!: /ChangeAccountPersonCount/ChangeAccountPersonCount/DocumentsToChangePersonCount
                                    
                    state: Yes_Contacts
                        q: $yes
                        q: $agree
                        q: $Yes_for_contacts
                        event: noMatch
                        a:   Записывайте городские номера. Код города - 727. --- АлматыЭнергоСбыт - телефон 356, 99, 99. Алматинские тепловые сети, 341, 0, 777.  АлматыСу,  3, 777, 444. Алматыгаз,  244, 55,  33. Тартып - 393, 08, 03. Повторить номера?
                            
                        state: No_Repeat
                            q: $no
                            q: $disagree
                            go!: /ChangeAccountPersonCount/ChangeAccountPersonCount/DocumentsToChangePersonCount
                                
                        state: Yes_Repeat
                            q: $yes
                            q: $agree
                            go!: ../../Yes_Contacts
                    

        state: Online
            #intent: /Online
            q: * $Online *
            q: $yes
            q: $agree
            a: Это можно сделать на сайте смарт точка алсеко точка кей зет.
            a: Зайдите в личный кабинет через э це пэ собственника жилья. Выберите раздел Мои Заявки. Там создайте новую заявку, укажите Алсеко как поставщика услуг и выберите заявку. Дальше следуйте инструкции
            go!: /ИнициацияЗавершения/CanIHelpYou
            
        state: DocumentsToChangePersonCount
            a:  Перечислить необходимые документы?
            
            state: No_List_Doc
                q: $no
                q: $disagree
                # a:  Интент "Инициация завершения диалога"
                go!: /ИнициацияЗавершения/CanIHelpYou                                
                
            state: Yes_List_Doc
                q: $yes
                q: $agree
                a:  Необходимые документы: удостоверение личности собственника и сведения о зарегистрированных лицах с портала е гов
                go!: /ИнициацияЗавершения/CanIHelpYou

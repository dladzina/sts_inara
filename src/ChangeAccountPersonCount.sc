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

# !!!!!!
                state: No_Suppliers_List
                    q: $no
                    q: $disagree
                    event: noMatch
                    go!: /ChangeAccountPerson/ChangeAccountPerson/DocumentsForLandlords
                                
                state: Yes_Suppliers_List
                    # пользователь сказал, что хочет узнать контакты поставщиков 
                    #  уточняем, есть ли ЛС. Если нет, то даем контакты всех
                    # если говорит номер ЛС, то даем только тех, что есть в квитанции
                    q: $yes

        state: Online
            #intent: /Online
            q: * $Online *
            q: $yes
            q: $agree
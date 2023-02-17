require:  Functions/SupplContacts.js



theme: /SupplierContacts
    state: SupplierContacts
        intent!: /КонтактыПоставщика
        script: 
            SupplContactsClear();

            # если есть услуга, то выделяем ее 
            if ($parseTree._Услуга){
                SupplContactsSetServ($parseTree._Услуга[0].SERV_ID)
                # $reactions.answer(toPrettyString($parseTree._Услуга));
            }
            else if ($parseTree._ОсновнойПоставщик){
                SupplContactsSetSuppl($parseTree._ОсновнойПоставщик[0])
            }

        a: даем контакты по услуге
        if: SupplContactsIsSuppSet()
            go!: SupplierContactsSayContacts
            # a: Записывайте. {{GetMainSupplNamesContact($MainSuppl,SupplContactsGetSupplCode())}}.
        #  если есть ЛС, то смотрим по нему. если ЛС нет, то надо спрашивать
        # смотрим, был ли лицевой счет выявлен в ходе диалога
        # Есть номер лицевого счета, будем давать информацию по нему по контактам поставщиков
        elseif: FindAccountIsAccountSet()
            go!: SupplierContactsByAccountServ
        else: 
            # здесь идет определение, что ЛС в рамках дилагога еще не запрашивался - передаем управление туда
            a: Чтобы я дала контакты нужных Вам поставщиков, нужен Ваш лицевой счёт
            BlockAccountNumber:
                okState = SupplierContactsByAccountServ
                errorState = SupplierContactsError
                noAccountState = SupplierContactsError
            
        state: SupplierContactsError
            a: без лицевого счета не могу дать вам телефон поставщика
        
        state: SupplierContactsByAccountServ
            # если есть услуга, то ее не запрашиваем - сразу идем на определение кода 
            if: SupplContactsGetServices()
                go!: ../SupplierContactsSayContacts
            a: Назовите услугу
            
            state: SupplierContactsByAccountServGetServ
                q: * @Услуга * 
                a: {{toPrettyString($parseTree)}}
                script:
                    if ($parseTree._Услуга){
                        SupplContactsSetServ($parseTree._Услуга.SERV_ID)
                    } 
                if: SupplContactsGetServices()
                    go!:../../SupplierContactsSayContacts
                else:
                    a: Я не нашла услугу. Перевожу Вас на оператора
                
        
        state: SupplierContactsSayContacts
            script:
                $temp.ss = {};
                if (SupplContactsIsSuppSet())
                    $temp.ss.text = GetMainSupplNamesContact($MainSuppl,SupplContactsGetSupplCode())
                else 
                    SupplContactsGetContactsByAccountServ($temp.ss);
            # a: Сообщаем контакы
            # a: Запрос еще в работе {{$temp.ss.text}}. лицевой счет {{AccountTalkNumber($session.Account.Number)}}, услуга [{{toPrettyString(SupplContactsGetServices())}}]
            a: Записывайте. {{$temp.ss.text}}. Повторить? 
            intent: /Согласие || toState = "."
            intent: /Согласие_повторить || toState = "."
            intent: /Несогласие || toState = "../CanIHelpYou"
            intent: /Несогласие_повторить || toState = "../CanIHelpYou"
        
        state: CanIHelpYou 
            # CommonAnswers
            script:
                $temp.index = $reactions.random(CommonAnswers.CanIHelpYou.length);
            a: {{CommonAnswers.CanIHelpYou[$temp.index]}}
            # a: Нужна ли моя помощь дальше?
            
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
            
            
            # state: SupplierContactsSayContactsYes
            


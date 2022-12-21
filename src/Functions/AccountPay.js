//-----------------------------------------------------------------
// получает дату последней оплаты по ЛС
function GetAccountPayShortInfo(){
    var $session = $jsapi.context().session;
    var $injector = $jsapi.context().injector;
    var return_str = '';
    // есть сведения по ЛС 
    if ($session.Account && $session.Account.Number>0){
        // смотрим, была ли получена ранее информация по основным поставщикам
        //if (!$session.Account.MainSuppliers)
        {
            // функция для поиска поставщиков
            var url = $injector.MacrosUrl + "sheetURL=" + $injector.AccountTableURL + "&sheetName="+$injector.AccountSheetPayName
            url = url + "&filterHead=account_number&filterValue="+$session.Account.Number;
            try{
                var response =  $http.query(url, {method: "GET",
                    timeout: 20000        // таймаут выполнения запроса в мс
                });
            }
            catch(e){
                //$reactions.answer("Что-то сервер барахлит. ");
                log('--------------- произошла ошибка GetAccountPayShortInfo' );
                return false;
            };

            if(response.isOk){
                if (response.data && response.data.data){
                    $session.Account.PaymentInfo = $session.Account.PaymentInfo || {};
                    $session.Account.PaymentInfo.date_last_pay =  response.data.data[0].date_last_pay;
                    $session.Account.PaymentInfo.sum_last_pay =  response.data.data[0].sum_last_pay;
                    $session.Account.PaymentInfo.registration_date =  response.data.data[0].registration_date;
                    if ($session.Account.PaymentInfo.date_last_pay != "")
                        $session.Account.PaymentInfo.date_last_pay = Date($session.Account.PaymentInfo.date_last_pay);
                    log(toPrettyString($session.Account.PaymentInfo));
                    log(typeof $session.Account.PaymentInfo.date_last_pay)

                }
            }
        }
    }

    return (($session.Account) && ($session.Account.PaymentInfo));
}

// function DateConvertFuntion(){
//     var message = "";
//     var dat_options =  { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
//     var dat = new Date(currentDate()); 
//     message = "Последняя дата  ";
//     message += dat.toLocaleDateString("ru-RU", dat_options);
//     return message; 
// }

function GetPaymentAnswer(){
    var MonthNames=['январь','февраль','март','апрель','май','июнь','июль','август','сентябрь','октябрь','ноябрь','декабрь'];
    var $session = $jsapi.context().session;
    var message = "";
    if ($session.Account.PaymentInfo.date_last_pay != ""){
        var dat_options =  { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        var dat = new Date(currentDate()); //;new Date($session.Account.PaymentInfo.date_last_pay)
        message = "Последняя оплата - ";
        //message += dat.toLocaleDateString("ru-RU", { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
        message += dat.getDate() + ' ' + $nlp.inflect(MonthNames[dat.getMonth()], "gent");
        // message += ' дата ' + dat.getDate();
        
        // message += ' ' +capitalize($nlp.inflect('январь', "gent"))
        // message += ' ' +capitalize($nlp.inflect('февраль', "gent"))
        // message += ' ' +capitalize($nlp.inflect('март', "gent"))
        // message += ' ' +capitalize($nlp.inflect('апрель', "gent"))
        // message += ' ' +capitalize($nlp.inflect('май', "gent"))
        // message += ' ' +capitalize($nlp.inflect('июнь', "gent"))
        // message += ' ' +capitalize($nlp.inflect('июль', "gent"))
        // message += ' ' +capitalize($nlp.inflect('август', "gent"))
        // message += ' ' +capitalize($nlp.inflect('сентябрь', "gent"))
        // message += ' ' +capitalize($nlp.inflect('октябрь', "gent"))
        // message += ' ' +capitalize($nlp.inflect('ноябрь', "gent"))
        // message += ' ' +capitalize($nlp.inflect('декабрь', "gent"))
        //message +=  dat.format("MMMM");
        return message; //"Последняя оплата  " + $session.Account.PaymentInfo.date_last_pay;
    }
        
    return "Оплата не поступала более 2-х месяцев. Просим Вас погасить задолженность";
}
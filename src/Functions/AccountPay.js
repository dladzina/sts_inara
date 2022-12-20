//-----------------------------------------------------------------
// получает дату последней оплаты по ЛС
function GetAccountPayShortInfo(){
    var $session = $jsapi.context().session;
    var $injector = $jsapi.context().injector;
    var return_str = '';
    // есть сведения по ЛС 
    if ($session.Account && $session.Account.Number>0){
        // смотрим, была ли получена ранее информация по основным поставщикам
        if (!$session.Account.MainSuppliers){
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

                }
            }
        }
    }

    return (($session.Account) && ($session.Account.PaymentInfo));
}

function GetPaymentAnswer(){
    var $session = $jsapi.context().session;
    if ($session.Account.PaymentInfo.date_last_pay) 
        return "Последняя оплата поступила " + $session.Account.PaymentInfo.date_last_pay;
    return "Оплата не поступала более 2-х месяцев. Просим Вас погасить задолженность";
}
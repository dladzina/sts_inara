function AddRequestComplaint(){
    {
        // функция для поиска поставщиков
        // var url = $injector.MacrosUrl + "sheetURL=" + $injector.AccountTableURL + "&sheetName="+$injector.AccountSheetPayName
        // url = url + "&filterHead=account_number&filterValue="+$session.Account.Number;
        log("function AddRequestComplaint")
        var addr = $env.get("InaraSeviceAddress", "Адрес сервиса не найден") + 'requests/complaint';
        var url = addr;
        var token = $secrets.get("InaraSeviceToken", "Токен не найден")
        var request_params = {
            "accountId": GetAccountNumber(),
            "supplierCodeName": "Aes",
            "serviceCode": 13,
            "userText": $.request.query,
            "audioLink": $dialer.getCallRecordingFullUrl(),
            "userPhoneNumber": $dialer.getCaller(),
            "complaintType": "WRONG_PHONE_NUMBER"
        }

        try{
            var response =  $http.query(url, {method: "POST",
                timeout: 20000        // таймаут выполнения запроса в мс
                ,headers: {"Content-Type": "application/json", "Authorization": "Basic " + token}//dXNlcl9zZXJ2aWNlOk5TV0tvZ0RZX1BIcVZvNWM="
                , body: request_params
            });
        }
        catch(e){
            //$reactions.answer("Что-то сервер барахлит. ");
            log('--------------- произошла ошибка AddRequestComplaint' );
            SendErrorMessage("onHttpRequest", 'Функция: AddRequestComplaint')

            
            return false;
        };

        if(response.isOk){
            if (response.data /*&& response.data.data[0]*/){
                $session.Account.PaymentInfo = $session.Account.PaymentInfo || {};

                $session.Account.PaymentInfo.date_last_pay =  response.data.payDate;
                $session.Account.PaymentInfo.registration_date =  response.data.registrationDate;

                // $session.Account.PaymentInfo.date_last_pay =  response.data.data[0].date_last_pay;
                // $session.Account.PaymentInfo.sum_last_pay =  response.data.data[0].sum_last_pay;
                // $session.Account.PaymentInfo.registration_date =  response.data.data[0].registration_date;

            }
        }
        else{
            // произошла ошибка сервиса - надо залогировать
            SendErrorMessage("onHttpResponseError", toPrettyString(response.error))
        }
    }
}
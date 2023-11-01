package kg.devcats.processflowengine

import java.util.Date

object ProcessMocker {

    val mock = mutableMapOf<String, String>().apply {

        put(
            "start", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"IN_PROCESS",
          "appBarText":"Идентификация",
          "title":"Проверяем ваши \nдокументы",
          "description":""
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"ANY_BUTTON_ID",
                "text":"К идентификации",
                "style":"ACCENT",
                "disabled":true,
                "properties":{
                   
                }
             }
          },
          {
             "responseType":"RETRY",
             "responseItem":{
                "id":"RETRY_1",
                "properties": {
                    "showLoader":false,
                    "enableAt":${Date().time + 2000}
                }
             }
          }    
       ]
    }
""".trimIndent()
        )


        put(
            "retry1", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"COMPLETE",
          "appBarText":"Идентификация",
          "title":"Документы успешно \nпроверены",
          "description":"Перейдите к идендификации "
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"start_ident",
                "text":"К идентификации",
                "style":"ACCENT",
                "properties":{
                   "isEnabled":true
                }
             }
          }   
       ]
    }
""".trimIndent()
        )

        put(
            "start_ident", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"PASSPORT_FRONT_PHOTO",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"PASSPORT_FRONT_PHOTO",
                "text":"далее",
                "style":"ACCENT"
             }
          }   
       ]
    }
""".trimIndent()
        )

        put(
            "PASSPORT_FRONT_PHOTO", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"PASSPORT_BACK_PHOTO",
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"PASSPORT_BACK_PHOTO",
                "text":"Далее",
                "style":"ACCENT"
             }
          } 
       ]
    }
""".trimIndent()
        )

        put(
            "PASSPORT_BACK_PHOTO", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"SELFIE_PHOTO",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"SELFIE_PHOTO",
                "text":"Далее",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )

        put(
            "SELFIE_PHOTO", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"VIDEO_CALL_PROMO",
       "screen_state":{
          "status":null,
          "appBarText":"Идентификация",
          "title":"Осталось пройти \nвидеоидентификацию",
          "description":"Не забудьте приготовить паспорт."
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"RETURN_TO_QUEUE_LATER",
                "text":"Вернуться позже",
                "style":"SECONDARY"
             }
          },
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"VIDEO_IDENT_BUTTON",
                "text":"Пройти видеоидентификацию ",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )



        put(
            "VIDEO_IDENT_BUTTON", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"VIDEO_CALL",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"WEB_VIEW",
             "responseItem":{
                "id":"VIDEO_IDENT",
                "url":"https://mic-test.com"
             }
          }
       ]
    }
""".trimIndent()
        )

        put(
            "VIDEO_IDENT", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"IN_PROCESS",
          "appBarText":"Идентификация",
          "title":"Проверяем ваши \nдокументы",
          "description":""
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"WEB_VIEW_OFERTA",
                "text":"Далее ",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )


        put(
            "WEB_VIEW_OFERTA", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"WEB_VIEW",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"WEB_VIEW",
             "responseItem":{
                "id":"ANY_WEB_VIEW_2",
                "url":"https://lk.o.kg/uploads/dogovor_ru.pdf",
                "properties":{
                    "fileType":"PDF"
                }
             }
          }
       ]
    }
""".trimIndent()
        )

        put(
            "ANY_WEB_VIEW_2", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"IN_PROCESS",
          "appBarText":"Идентификация",
          "title":"Проверяем ваши \nдокументы",
          "description":""
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"OTP",
                "text":"Далее ",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )



        put(
            "OTP", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"INPUT_OTP",
       "screen_state":{
          "status":null,
          "appBarText":"Идентификация",
          "title":"",
          "description":"Подтвердите заявку.\n SMS с кодом отправлено на номер \n+996 700 000 999"
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"INPUT_FIELD",
             "responseItem":{
                "fieldId":"OTP_INPUT",
                "inputType":"NUMBER",
                "validations":[
                    {
                       "type":"REQUIRED",
                       "value":"true"
                    }
                ],
                "properties":{
                    "enableActionAfterMills":10000,
                    "additionalActionResolutionCode":"RESEND_OTP"
                }
             }
          }
       ]
    }
""".trimIndent()
        )


        put(
            "RESEND_OTP", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"INPUT_OTP",
       "screen_state":{
          "status":null,
          "appBarText":"Идентификация",
          "title":"",
          "description":"Подтвердите заявку.\n SMS с кодом отправлено на номер \n+996 700 000 999"
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"INPUT_FIELD",
             "responseItem":{
                "fieldId":"OTP_INPUT",
                "inputType":"NUMBER",
                "properties":{
                    "enableActionAfterMills":10000,
                    "additionalActionResolutionCode":"RESEND_OTP",
                    "errorMessage":"Не верный код ОТП"
                }
             }
          }
       ]
    }
""".trimIndent()
        )



        put(
            "OTP_INPUT", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"INPUT_FORM",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
            "responseType":"INPUT_FORM",
            "responseItem":{
                "formId":"passport_form",
                "title":"Паспортные данные",
                "formItems":[
                    {
                        "formItemType":"INPUT_FIELD",
                        "formItem":{
                            "fieldId":"NAME",
                            "label":"Введите заглавные буквы и номер без пробелов. Например: AN1234567, ID1234567",
                            "placeholder":"Имя",
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ]
                        }
                    },
                    {
                        "formItemType":"DROP_DOWN_FORM_ITEM",
                        "formItem":{
                            "fieldId":"CHOOSE_REGION",
                            "chooseType":"SINGLE",
                            "label":"Регион",
                            "isNeedToFetchOptions":true,
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ]
                        }
                    },
                    {
                        "formItemType":"DROP_DOWN_FORM_ITEM",
                        "formItem":{
                            "fieldId":"CHOOSE_CITY",
                            "parentFieldId":"CHOOSE_REGION",
                            "chooseType":"SINGLE",
                            "label":"Город",
                            "isNeedToFetchOptions":true,
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ]
                        }
                    },
                    {
                        "formItemType":"INPUT_FIELD",
                        "formItem":{
                            "fieldId":"INN",
                            "label":"Персональный номер, состоящий из 14 цифр",
                            "placeholder":"ИНН",
                            "validations":[
                                {
                                    "type":"REGEX",
                                    "value":"^[0-9]*"
                                },
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ]
                        }
                    }
                ]
            }
          }
       ]
    }
""".trimIndent()
        )


        put(
            "passport_form", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"INPUT_FORM",
       "screen_state":{},
       "messages":[],
       "allowed_answers":[
          {
            "responseType":"INPUT_FORM",
            "responseItem":{
                "formId":"address",
                "title":"Адрес проживания",
                "formItems":[
                    {
                        "formItemType":"INPUT_FIELD",
                        "formItem":{
                            "fieldId":"NAME",
                            "placeholder":"No required"
                        }
                    },
                    {
                        "formItemType":"INPUT_FIELD",
                        "formItem":{
                            "fieldId":"NAME2",
                            "placeholder":"No required",
                            "value":"Default"
                        }
                    },
                    {
                        "formItemType":"DROP_DOWN_FORM_ITEM",
                        "formItem":{
                            "fieldId":"CHOOSE_REGION",
                            "chooseType":"SINGLE",
                            "label":"Регион",
                            "isNeedToFetchOptions":true,
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ]
                        }
                    },
                    {
                        "formItemType":"DROP_DOWN_FORM_ITEM",
                        "formItem":{
                            "fieldId":"CHOOSE_CITY",
                            "parentFieldId":"CHOOSE_REGION",
                            "chooseType":"SINGLE",
                            "label":"Город",
                            "isNeedToFetchOptions":true,
                            "validations":[
                                {
                                    "type":"REQUIRED",
                                    "value":"true"
                                }
                            ]
                        }
                    }
                ]
            }
          }
       ]
    }
""".trimIndent()
        )


        put(
            "address", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"COMPLETE",
          "appBarText":"Идентификация",
          "title":"Документы успешно \nсформированы",
          "description":""
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"CREATE_qweqwew_APPLICATION",
                "text":"Создать заявку",
                "style":"ACCENT",
                "properties":{
                    
                }
             }
          }
       ]
    }
""".trimIndent()
        )

        put(
            "CREATE_qweqwew_APPLICATION", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"WEB_VIEW",
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"WEB_VIEW",
             "responseItem":{
                "id":"ANY_WEB_VIEW_ID",
                "url":"https://google.com"
             }
          }
       ]
    }
""".trimIndent()
        )

        put(
            "ANY_WEB_VIEW_ID", """
    {
       "process_id":"werq-rqwew-rwer-fser",
       "process_status":"RUNNING",
       "screen_code":"STATUS_INFO",
       "screen_state":{
          "status":"COMPLETE",
          "appBarText":"Идентификация",
          "title":"Документы успешно \nсформированы",
          "description":""
       },
       "messages":[],
       "allowed_answers":[
          {
             "responseType":"BUTTON",
             "responseItem":{
                "buttonId":"EXIT_NAVIGATE_TO_WALLET_MAIN",
                "text":"На главную",
                "style":"ACCENT"
             }
          }
       ]
    }
""".trimIndent()
        )

    }

}
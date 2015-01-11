function DatepickerInitialize(datepicker1, datepicker1text, datepicker2, datepicker2text, peoplePicker, dateInCookies, dateOutCookies, people, button, ajax) {

    if(typeof(ajax)==='undefined') ajax = true;

    // Set Array of Months
    var fullMonthArray = $.datepicker.regional['ru'].monthNames;

    // Retrive session and parse it to Date
    var dateIn = new Date();
    var dateOut = new Date();
    dateOut.setDate(dateOut.getDate()+3); // Plus 3 days

    if(!dateInCookies.length == 0) {
        dateIn = $.datepicker.parseDate("D M dd yy", dateInCookies, {
            dayNamesShort: $.datepicker.regional[ "" ].dayNamesShort,
            monthNamesShort: $.datepicker.regional[ "" ].monthNamesShort,
        });
    }

    if(!dateOutCookies.length == 0) {
        dateOut = $.datepicker.parseDate("D M dd yy", dateOutCookies, {
            dayNamesShort: $.datepicker.regional[ "" ].dayNamesShort,
            monthNamesShort: $.datepicker.regional[ "" ].monthNamesShort,
        });
    }

    /*$.datepicker.setDefaults(
        $.extend($.datepicker.regional["ru"])
    );*/

    function SetEvents(datepicker, datepicker2, text) {
        var dpicker = $(datepicker).find(".ui-datepicker");
        var dpicker2 = $(datepicker2).find(".ui-datepicker");

        $(text).change(function(){
            $(datepicker).datepicker('setDate', $(this).val());
        });

        $(text).click(function(e){
            if (dpicker.is(":visible"))
            {
                dpicker.hide();
                dpicker2.hide();
            } else {
                dpicker.show();
                dpicker2.hide();
            }
            return false;
        });

        $(document).click(function (e)
        {
            e.stopPropagation();
            if (dpicker.has(e.target).length === 0)
            {
                dpicker.hide();
            }
        });

        $(".ui-datepicker").click(function(e){
            e.stopPropagation();
        });

        $(datepicker).change(function(){
            $(text).attr('value',$(this).val());
        });
    }

    $(datepicker1).datepicker({
        defaultDate: dateIn,
        minDate: 0,
        altField: datepicker1text,

        onSelect: function(date) {
            // give second datepicker minDate
            $(datepicker2).datepicker('option', 'minDate', date);
            $(this).find(".ui-datepicker").fadeOut('fast');
        }
    }, $.datepicker.regional['ru']);

    $(datepicker2).datepicker({
            defaultDate: dateOut,
            minDate: dateIn,
            altField: datepicker2text,

            onSelect: function(date) {
                $(this).find(".ui-datepicker").fadeOut('fast');
            }
        },
        $.datepicker.regional['ru']
    );

    // Handle all changes to datepickers

    SetEvents(datepicker1, datepicker2, datepicker1text);

    SetEvents(datepicker2, datepicker1, datepicker2text);

    // Events for textfield

    var holder = $(peoplePicker).val();

    $(peoplePicker).focus(function()
    {
            holder = $(this).val();
            this.value="";
    });

    $(peoplePicker).focusout(function()
    {
        if($.trim($(this).val())=='') {
            this.value = holder;
        }
    });

    $(".ui-datepicker").hide();

    // number event handler

    $(datepicker1).on("blur", function(e) { $(this).datepicker("hide"); });
    $(datepicker2).on("blur", function(e) { $(this).datepicker("hide"); });

    if(ajax) {
        $(button).click(function(e){

            //save all data from pickers

            bookIn = $(datepicker1).datepicker('getDate' ).toDateString();
            bookOut = $(datepicker2).datepicker('getDate' ).toDateString();
            var ppl = $(peoplePicker).val();

            //convert to JSON
            var obj = {};
            obj = {
                dateIn: bookIn,
                dateOut: bookOut,
                people: ppl
            };
            var jsonData = JSON.stringify(obj);

            $.ajax({
                type: "POST",
                url: "/booking/step1",
                dataType: "text",
                data: jsonData,
                contentType: "application/json; charset=utf-8"
            }).done(function ( data ) {
                window.location.href = '/booking/step2';
            }).fail(function ( data ) {
                alert("Извините, наши серверы временно недоступны. Пожалуйста, оформите заказ по телефону.");
            });

            e.preventDefault();

        });
    }

}
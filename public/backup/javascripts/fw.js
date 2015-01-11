function FortuneWheel ( config ) {

    var touch = isTouchDevice();

    var stage, canvas, ctx, wheel, wedge,
        touchHandler, dragHandler, releaseHandler,
        aRequest, mDown,
        IMAGES = {},
        Pi = Math.PI,
        FW = this;

    ( function init(z) {

        stage = new Stage(z);
        if ( stage.blah ) {
            canvas = stage.canvas;
            ctx = canvas.getContext("2d");
            loadImages(z, function() {
                wheel = new Wheel(stage, z);
                wedge = new Wedge(z);
                ctx.drawImage(wheel.canvas, stage.cX - wheel.w/2, stage.cY - wheel.h/2);
                ctx.drawImage(wedge.canvas, stage.cX - wedge.w/2, stage.cY - wheel.h/2);
                // drawWedge( ctx, stage.cX, stage.cY - wheel.h/2, z.wedgeColor );
            });
            setEventListeners();
            setAnimRequest();
        }
    } ) (config);

    function setEventListeners() {

        touchHandler = Hammer(canvas).on("touch", function(event) {
            var pointer = getPointerPos(canvas, event);
            onMouseDown(pointer.x, pointer.y);
        });

        dragHandler = Hammer(canvas).on("drag", function(event) {
            event.gesture.preventDefault();
            var pointer = getPointerPos(canvas, event);
            onMouseMove(pointer.x, pointer.y);
        });

        releaseHandler = Hammer(canvas).on("release", function(event) {
            var pointer = getPointerPos(canvas, event);
            onMouseUp(pointer.x, pointer.y);
        });
    }

    function removeEventListeners() {
        touchHandler.enable(false);
        dragHandler.enable(false);
        releaseHandler.enable(false);
    }



    function getPointerPos(c, e) {
        var bcr = c.getBoundingClientRect();
        return {
            x: e.gesture.center.pageX - bcr.left,
            y: e.gesture.center.pageY - bcr.top
        };
    }

    function onMouseDown(x, y) {
        var inside = wheel.getDistance (x, y),
            dx, dy, d, ts, ang;
        if ( inside ) {
            mDown = true;
            stage.canvas.style.cursor = "-webkit-grabbing";
            stage.canvas.style.cursor = "-webkit-grabbing";
            stage.canvas.style.cursor = "-moz-grabbing";
            dx = x - stage.cX;
            dy = y - stage.cY;
            ang = Math.atan2(dy, dx);
            stage.startAngle = ( ang > 0 ) ? ang : 2*Pi + ang;
            stage.angVelocity = 0;
        }
    }
    function onMouseUp(x, y) {
        mDown = false;
        var speed = Math.abs(stage.angVelocity),
            dx, dy, ang;
        if ( speed ) {
            dx = x - stage.cX;
            dy = y - stage.cY;
            ang = Math.atan2(dy, dx);
            ang = ( ang > 0 ) ? ang : 2*Pi + ang;
            if ( speed > 1 ) {
                showAlert("Задержите дыхание и ждите!");
                spin(ctx, ang - stage.startAngle, stage.angVelocity);
                removeEventListeners();
                stage.canvas.style.cursor = "wait";
            }else {
                showAlert("Слишком медленно, крутите сильнее!");
                stage.canvas.style.cursor = "auto";
            }
        }else {
            stage.canvas.style.cursor = "auto";
        }
    }

    function onMouseMove(x, y) {
        var inside = wheel.getDistance (x, y),
            dx, dy, ang, d, ts, timeDelta, angDelta;
        if ( mDown && inside ) {
            dx = x - stage.cX;
            dy = y - stage.cY;
            ang = Math.atan2(dy, dx);
            ang = ( ang > 0 ) ? ang : 2*Pi + ang;
            d = new Date();
            ts = d.getTime();
            stage.time = stage.time || 0;
            stage.angle = stage.angle || stage.startAngle;
            timeDelta = ts - stage.time;
            angDelta = ang - stage.angle;
            drag(ctx, ang - stage.startAngle);
            if ( timeDelta > 100 ) {
                stage.angVelocity = angDelta/timeDelta*1000;
                stage.time = ts;
                stage.angle = ang;
            }
        }
    }


    function loadImages(conf, callback) {
        var arr = conf.sectors,
            imArr = [],
            nameArr = [],
            len = arr.length,
            i = 0;
        for ( ; i<len; i++ ) {
            imArr.push(arr[i].img);
            nameArr.push(arr[i].name);
        }
        loadImage(imArr, nameArr, callback);
    }

    function loadImage(iarr, narr, callback) {
        var img = new Image(),
            name = narr.shift();
        img.src = iarr.shift();
        img.onload = function(){
            IMAGES[name] = img;
            if ( iarr.length ) {
                loadImage(iarr, narr, callback);
            }else {
                callback();
            }
        };
    }


    function Stage(conf) {
        var container = document.getElementById(conf.container),
            congratsBlock = document.getElementById(conf.congratsBlock),
            secondTryBlock = document.getElementById(conf.secondTryBlock),
            secondTryText = document.getElementById("secondTryText"),
            byeBlock = document.getElementById(conf.byeBlock),
            headerBlock = document.getElementById("headline2"),
            state = getState(conf.timeToWait),
            canvas, radius, viewHeight, relWidth;
        if ( state && container && congratsBlock && secondTryBlock && byeBlock ) {
            this.config = conf;
            this.state = state;
            if ( state=="chance" ) {
                secondTryText.textContent = "Попробуйте еще раз!"
                secondTryBlock.style.display = "block";
            }
            this.container = container;
            this.congratsBlock = congratsBlock;
            this.secondTryBlock = secondTryBlock;
            this.byeBlock = byeBlock;
            this.w = container.clientWidth;
            this.headerBlock = headerBlock;
            relWidth = ( touch && this.w < 800 ) ? conf.mobileRelWidth : conf.relWidth;
            radius = (this.w * relWidth)/2;
            this.viewHeight = (1 - conf.centerDepth) * radius;
            this.h = this.viewHeight * (1 + conf.emptySpace);
            canvas = document.createElement("canvas");
            canvas.width = this.w;
            container.style.height = Math.ceil(this.h) + "px";
            canvas.height = this.h;
            container.appendChild(canvas);
            canvas.style.position = "absolute";
            canvas.style.left = 0;
            canvas.style.top = 0;
            this.canvas = canvas;
            this.cX = this.w/2;
            this.cY = this.h  + conf.centerDepth * radius;
            setFormEvents(this);
            this.blah = state;
        }else {
            if ( container ) { container.style.display = "none"; headerBlock.style.display = "none"; }
            FW = null;
            return false;
        }
    }

    function getState(ttw) {
        var mark = localStorage.getItem("mark"),
            cooky = getCookie("mark"),
            waitime = ttw*1000*2,//*3600,
            time, current, arr, diff;
        if ( cooky ) {
            if ( cooky==="chance" ) {
                return "chance";
            }else {
                return false;
            }
        }
        if ( mark ) {
            arr = mark.split("-");
            if ( arr[0]==="chance" ) { return "chance"; }
            current = new Date().getTime();
            time = +arr[1];
            diff = current - time;
            if ( diff < waitime ) { return false; }
        }
        return "start";
    }

    function getCookie(nm) {
        var re = new RegExp("[; ]"+nm+"=([^\\s;]*)"),
            match = (" "+document.cookie).match(re);
        if ( nm && match ) { return unescape(match[1]); }
        return "";
    }

    function setFormEvents(stg) {
        var forms = document.forms,
            congForm = forms.congratsForm,
            stForm = forms.secondTryForm,
            congInput = congForm.email,
            stInput = stForm.email,
            congSubmit = congForm.submit,
            stSubmit = congForm.submit;

        congForm.addEventListener("submit", function(event) {
            event.preventDefault();
            var email = validateEmail(congInput.value);
            if ( email ) {
//-----------------------
                FW.email(email, stg.state);
//-----------------------
                var chao = stg.byeBlock.getElementsByTagName("p")[0];
                chao.textContent = "Проверьте почту";
                stg.byeBlock.style.display = "block";

                //jQuery

                var btn = $('#congratsBlock');
                var tryBlock = $('#secondTryBlock');

                tryBlock.animate({opacity: 0}, 500);

                // Preloader
                btn.find('.to-hide' ).animate({opacity: 0}, 500);

                btn.find('.preloader').css({
                    'display' : 'block',
                    'position' : 'absolute',
                    'left' : '50%',
                    'top' : '50%',
                    'margin-left' : -$('.preloader').width()/2,
                    'margin-top' : -$('.container').height()/2
                });

                var obj = {
                    email: btn.find('.newstxt' ).val(),
                    prize: btn.find('.prizeInfo' ).html()
                };

                jsRouter.controllers.RoomController.wheelCongrats().ajax({
                    type: "POST",
                    data: JSON.stringify(obj),
                    contentType: "application/json; charset=utf-8",
                    dataType: "text",
                    success: function(response) {
                        btn.find('.to-hide' ).hide();
                        btn.find('.preloader').hide();
                        btn.find('.booking-thankyou').addClass('animated bounceInLeft');
                        btn.find('.booking-thankyou img').addClass('animated rollIn');
                        btn.find('.booking-thankyou' ).show();
                        setTimeout(function() {
                            btn.hide();
                        }, 10000);
                    },
                    error: function(response) {
                        btn.find('.preloader').hide();
                        alert("Извините, наши серверы временно недоступны. Пожалуйста, позвоните нам по телефону и расскажите о выигрыше.");
                    }
                });

                setTimeout(function() {
                    stg.container.style.display = "none";
                }, 3000);
            }else {
                congInput.value = "Введите правильный адрес";
            }
        });

        stForm.addEventListener("submit", function(event) {
            event.preventDefault();
            var email = validateEmail(stInput.value);
            if ( email ) {
//-----------------------
                FW.email(email);
//-----------------------
                setMarks(stg);
                congInput.value = email;
                touchHandler.enable(true);
                dragHandler.enable(true);
                releaseHandler.enable(true);
                showAlert("Попробуйте еще раз!");

                stg.secondTryBlock.style.display = "none";

                //jQuery

                var btn = $('#secondTryBlock');

                // Preloader
                btn.find('.to-hide' ).animate({opacity: 0}, 500);

                var obj = {
                    email: btn.find('.newstxt' ).val()
                };

                jsRouter.controllers.RoomController.wheelTry().ajax({
                    type: "POST",
                    data: JSON.stringify(obj),
                    contentType: "application/json; charset=utf-8",
                    dataType: "text",
                    success: function(response) {

                    },
                    error: function(response) {
                        alert("Извините, наши серверы временно недоступны. Пожалуйста, позвоните нам по телефону и расскажите о выигрыше.");
                    }
                });

            }else {
                stInput.value = "Введите правильный адрес";
            }
        });
    }


    function setMarks(stg) {
        var time = new Date().getTime(),
            mark = stg.state + "-" + time;
        document.cookie = "mark=" + stg.state + "; max-age=48" + (stg.config.timeToWait*2);
        localStorage.setItem("mark", mark);
    }

    function validateEmail(email) {
        var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email) && email;
    }

    function Wedge(conf) {

        var canvas, ct;

        var self = this;

        (function wedgeInit(a) {
            canvas = document.createElement("canvas");
            canvas.width = canvas.height = a.wedgeSize + 10;
            self.w = a.wedgeSize + 10;
            ct = canvas.getContext("2d");
            self.canvas = canvas;
            drawWedge(ct, a);
        }) (conf);

        function drawWedge(ctx, conf) {
            var size = conf.wedgeSize;
            ctx.shadowBlur = 10;
            ctx.shadowColor = "black";
            ctx.fillStyle = conf.wedgeColor;
            console.log(ctx);
            ctx.beginPath();
            ctx.moveTo(5, 0);
            ctx.lineTo(size+5, 0);
            ctx.lineTo(size/2+5, size+5);
            ctx.fill();
        }
    }

    function Wheel(stg, conf) {

        var canvas, ctx, wheel, array, aRequest, num;

        var self = this;

        (function wInit(a,b) {
            var relWidth = ( touch && a.w < 800 ) ? b.mobileRelWidth : b.relWidth;
            canvas = document.createElement("canvas");
            canvas.width = canvas.height = a.w * relWidth;
            ctx = canvas.getContext("2d");
            var arrays = getArrays(b);
            self.canvas = canvas;
            self.array = arrays[0];
            self.discounts = arrays[1];
            self.w = canvas.width;
            self.h = canvas.height;
            self.r = self.w/2;
            drawWF (ctx, b);
        }) (stg, conf);

        this.getDistance = function(x, y) {
            var dx = Math.abs(x - stg.cX ),
                dy = Math.abs(y - stg.cY),
                dist = Math.sqrt(dx*dx + dy*dy);
            return ( dist < this.r ) ? dist : false;
        };

        function getArrays(o) {
            var arr = conf.sectors,
                newarr = [],
                discnt = {},
                len = arr.length,
                i = 0;
            for ( ; i<len; i++ ) {
                discnt[arr[i].name] = arr[i].discount;
                while (arr[i].num) {
                    newarr.push(arr[i].name);
                    arr[i].num--;
                }
            }
            newarr = shuffle(newarr);
            num = newarr.length;
            return [newarr, discnt];
        }

        function drawWF (ctx, o) {
            var half = ctx.canvas.width/2,
                array = self.array,
                discounts = self.discounts,
                r = half - o.spokeWidth,
                angStep = 2*Pi/num,
                i = 0,
                imSize = Math.round(o.imageSize*ctx.canvas.width/num),
                imageRadius = r*o.imageRadius,
                textRadius = imageRadius - 1.05*imSize,
                startAng = 0,
                endAng = 2*Pi,
                text, img;
            ctx.save();
            ctx.strokeStyle = o.strokeColor;
            ctx.lineWidth = o.spokeWidth;
            ctx.font = "bold " + 0.4*imSize + "px sans-serif";
            ctx.textAlign = "center";
            ctx.translate(half, half);
            for ( ; i < num; startAng += angStep, i++ ) {
                ctx.fillStyle = ( i%2 ) ? o.fillColor1 : o.fillColor2;
                ctx.beginPath();
                ctx.arc(0, 0, r, startAng, startAng + angStep);
                ctx.lineTo(0, 0);
                ctx.fill();
            }
            i = 0;
            startAng = 0;
            for ( ; i < num; startAng += angStep, i++ ) {
                text = discounts[array[i]];
                img = IMAGES[array[i]];
                ctx.fillStyle = ( i%2 ) ? o.fillColor1 : o.fillColor2;
                ctx.save();
                ctx.rotate(startAng);
                ctx.beginPath();
                ctx.arc(0, 0, r, 0, angStep);
                ctx.lineTo(0, 0);
                ctx.closePath();
                ctx.stroke();
                ctx.fillStyle = o.strokeColor;
                // if ( num%4 ) { ctx.rotate(-angStep/2); }
                ctx.rotate(angStep/2);
                ctx.fillText(text, 0, -textRadius);
                ctx.translate(0, -imageRadius);
                ctx.rotate(-angStep);
                ctx.drawImage(img, -imSize/2, -imSize/2, imSize, imSize);
                ctx.restore();
            }
            ctx.restore();
            self.wedgeAngle = angStep;
        }
    }


    function showAlert(s) {
        var headline = stage.container.getElementsByClassName("headline")[0];
        headline.textContent = s;
    }

    function spin(ctx, ang, velocity) {
        var r = wheel.r,
            canvas = wheel.canvas,
            wcanvas = wedge.canvas,
            x = stage.cX,
            y = stage.cY;
        aRequest = requestAnimationFrame( update );
        function update() {
            ctx.save();
            ctx.translate(x, y);
            ctx.rotate(ang);
            ctx.drawImage(canvas, -r, -r);
            ctx.restore();
            ctx.drawImage(wcanvas, x - wedge.w/2, y - wheel.h/2);
            ang += velocity/100;
            velocity *= 0.995;
            if ( Math.abs(velocity) > 0.05 ) {
                aRequest = requestAnimationFrame( update );
            }else {
                checkPrize(ang);
                stage.canvas.style.cursor = "auto";

            }
        }
    }

    function checkPrize(ang) {
        var arr = wheel.array;
        var step = wheel.wedgeAngle;
        var full = 2*Pi;
        if (ang>0) {
            while (ang>full) {
                ang -= full;
            }
            ang = full - ang;
        }else {
            ang = Math.abs(ang);
            while (ang>full) {
                ang -= full;
            }
        }
        var i = 0;
        while (step + step*i < ang ) { i++; }

        var angle = Math.round(ang*180/Pi);
        var prize = arr[i];
        setTimeout(function() {
            showBlock(prize);
        }, 2000);
    }

    function showBlock(prize) {
        var sectors = stage.config.sectors,
            len = sectors.length,
            i = 0, sector, discount,
            elem, prizeName, prizeInfo;
        if ( prize!=="loss" ) {
            elem = stage.congratsBlock;
            prizeName = elem.getElementsByClassName("prizeName")[0];
            prizeInfo = elem.getElementsByClassName("prizeInfo")[0];
            for ( ; i<len; i++ ) {
                if ( sectors[i].name === prize ) {
                    sector = sectors[i];
                    break;
                }
            }
            stage.state = prize + wheel.discounts[prize];
            setMarks(stage);
            discount = ( wheel.discounts[prize] ) ? " discount " + wheel.discounts[prize] + "%" : "";
            prizeName.textContent = prize;
            prizeInfo.textContent = sector.description;
        }else if ( stage.state==="start" ) {
            stage.state = "chance";
            setMarks(stage);
            elem = stage.secondTryBlock;
        }else {
            elem = stage.byeBlock;
            setTimeout(function() {
                stg.state = "loss";
                setMarks(stage);
                stg.container.style.display = "none";
            }, 3000);
        }
        elem.style.display = "block";
        stage.headerBlock.style.display = "none";
    }

    function drag(ctx, ang) {
        ctx.save();
        ctx.translate(stage.cX, stage.cY);
        ctx.rotate(ang);
        ctx.drawImage(wheel.canvas, -wheel.r, -wheel.r);
        ctx.restore();
        ctx.drawImage(wedge.canvas, stage.cX - wedge.w/2, stage.cY - wheel.h/2);
    }

    function shuffle(o) {
        for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
        return o;
    }

    function isTouchDevice() {
        return !!("ontouchstart" in window) || !!("onmsgesturechange" in window);
    }

    function setAnimRequest() {
        var lastTime = 0,
            i = 0,
            vendors = ["ms", "moz", "webkit", "o"];
        for ( ; i < vendors.length && !window.requestAnimationFrame; ++i) {
            window.requestAnimationFrame = window[vendors[i]+"RequestAnimationFrame"];
            window.cancelAnimationFrame = window[vendors[i] + "CancelAnimationFrame"] || window[vendors[i] + "CancelRequestAnimationFrame"];
        }
        if ( !window.requestAnimationFrame ) {
            window.requestAnimationFrame = function(callback, element) {
                var currTime = new Date().getTime(),
                    timeToCall = Math.max(0,16 - (currTime - lastTime)),
                    id = window.setTimeout(function() { callback(currTime + timeToCall); }, timeToCall);
                lastTime = currTime + timeToCall;
                return id;
            };
        }
        if ( !window.cancelAnimationFrame ) {
            window.cancelAnimationFrame = function( id ) {
                clearTimeout( id );
            };
        }
    }

}
// green 468966
// FFF0A5 FFB03B B64926 8E2800
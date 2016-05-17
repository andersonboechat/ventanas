var originalVal = $.fn.val;
$.fn.val = function (value) {
    if (typeof value == 'undefined') {
        // Getter

        if ($(this).is("textarea")) {
            return originalVal.call(this)
                .replace(/\r\n/g, '\n')     // reduce all \r\n to \n
                .replace(/\r/g, '\n')       // reduce all \r to \n (we shouldn't really need this line. this is for paranoia!)
                .replace(/\n/g, '\r\n');    // expand all \n to \r\n

            // this two-step approach allows us to not accidentally catch a perfect \r\n
            //   and turn it into a \r\r\n, which wouldn't help anything.
        }

        return originalVal.call(this);
    }
    else {
        // Setter
        return originalVal.call(this, value);
    }
};
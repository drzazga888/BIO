$(document).ready(function() {

    var sections = [];
    var nav = $('body').children('nav');
    var links = nav.find('li:not(.has-sub)').find('a');
    var have_subs = nav.find('li.has-sub').find('a');
    var links_all = links.add(have_subs);
    var main = $('main');
    var block_headers = main.find('.block > header, .sub-block > header');

    links.each(function() {
        var link = $(this);
        sections.push({
            nav: link,
            main: $(link.attr('href'))
        });
    });

    main.on('scroll resize', function() {
        var i = 0;
        while (i < sections.length) {
            var elem = sections[i].main;
            while (!elem.is(':visible')) {
                elem = elem.parent();
            }
            var offset = elem.offset().top;
            if (offset >= 0) {
                break;
            }
            ++i;
        }
        links_all.removeClass('current');
        sections[i].nav.parentsUntil(nav, 'li').children('a').addClass('current');
    }).trigger('scroll');

    $('#menu-switcher').on('click', function() {
        nav.toggleClass('showed');
    });

    links.on('click', function() {
        nav.removeClass('showed');
        $($(this).attr('href')).parentsUntil(main, '.block, .sub-block').addClass('showed');
    });

    have_subs.on('click', function() {
        $(this).parent().toggleClass('showed');
    });

    links_all.filter('.function').html(function(_, oldhtml){
        return oldhtml.replace(/_/g, '_<wbr />');
    });

    block_headers.on('click', function() {
        $(this).parent().toggleClass('showed');
    });

    var hash;
    if (hash = ($(location).attr('hash'))) {
        var hashLink = $('a[href="' + hash + '"');
        var hashBlock = $(hash);
        hashLink.parentsUntil(nav, 'li.has-sub').addClass('showed');
        hashBlock.parentsUntil(main, '.block, .sub-block').addClass('showed');
        $(main).scrollTop(hashBlock.offset().top);
    }

});

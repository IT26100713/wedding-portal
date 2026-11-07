// Indices refer to the existing VendorService lists; prices come from Thymeleaf.
(() => {
    const keys = ['photographer', 'venue', 'caterer', 'decorator'];
    const selects = keys.map(key => document.getElementById(key));
    const guests = document.getElementById('guestCount');
    const cards = [...document.querySelectorAll('[data-package]')];
    const combinations = [[0,1,1,0], [1,0,0,1], [2,2,2,2], [3,1,1,2], [1,3,3,3]];
    const money = value => 'Rs. ' + value.toLocaleString('en-LK', {maximumFractionDigits: 0});
    const count = () => Math.max(0, Number(guests.value) || 0);
    const optionsFor = indices => indices.map((index, i) => selects[i].options[index + 1]);
    const total = options => options.reduce((sum, option, i) => sum + Number(option?.dataset.price || 0) * (i === 2 ? count() : 1), 0);
    function refresh() {
        cards.forEach((card, index) => {
            const options = optionsFor(combinations[index]);
            const list = card.querySelector('.vendor-list');
            list.replaceChildren(...options.map((option, i) => {
                const item = document.createElement('li');
                item.textContent = `${keys[i][0].toUpperCase() + keys[i].slice(1)}: ${option.value}`;
                return item;
            }));
            card.querySelector('.package-price').textContent = money(total(options));
            card.querySelector('.guest-label').textContent = count();
            const selected = options.every((option, i) => option.value === selects[i].value);
            card.classList.toggle('is-selected', selected);
            const button = card.querySelector('button');
            button.setAttribute('aria-pressed', String(selected));
            button.textContent = selected ? 'Package selected' : 'Choose package';
        });
        document.getElementById('booking-total').textContent = money(total(selects.map(select => select.selectedOptions[0])));
        document.getElementById('selection-label').textContent = `Estimated total · ${count()} guests`;
    }
    cards.forEach((card, index) => card.querySelector('button').addEventListener('click', () => {
        optionsFor(combinations[index]).forEach((option, i) => { selects[i].value = option.value; });
        refresh();
        document.getElementById('booking-details').scrollIntoView({behavior: 'smooth', block: 'start'});
    }));
    selects.forEach(select => select.addEventListener('change', refresh));
    guests.addEventListener('input', refresh);
    const date = document.getElementById('eventDate');
    const today = new Date();
    date.min = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}-${String(today.getDate()).padStart(2,'0')}`;
    refresh();
})();

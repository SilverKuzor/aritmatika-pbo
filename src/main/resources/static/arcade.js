'use strict';

// GIF references from ZekaiDev/anime-reaction-gif. See THIRD-PARTY.md.
const library = 'https://github.com/ZekaiDev/anime-reaction-gif';
const operations = {
  '+': ['Penjumlahan', 'clap', 660],
  '-': ['Pengurangan', 'pout', 440],
  '*': ['Perkalian', 'dance', 880],
  '/': ['Pembagian', 'confused', 550]
};
const digits = ['peek', 'cool', 'blush', 'laugh', 'cheers',
  'wink', 'roll', 'happy', 'headbang', 'celebrate'];

const form = document.querySelector('#calculator');
const operator = form.elements.operator;
const gif = document.querySelector('#gif');
const symbol = document.querySelector('#symbol');
const caption = document.querySelector('#caption');
const source = document.querySelector('#gif-source');
const motion = document.querySelector('#motion');
const sound = document.querySelector('#sound');
const reduceMotion = matchMedia('(prefers-reduced-motion: reduce)');
let motionOn = !reduceMotion.matches;
let soundOn = false;
let audio;
let timer;
let reaction = null;

function refreshGif() {
  gif.hidden = true;
  if (!motionOn) {
    gif.removeAttribute('src');
    return;
  }
  gif.src = 'https://raw.githubusercontent.com/ZekaiDev/anime-reaction-gif/main/' + reaction;
}

function show(label, category, mark) {
  clearTimeout(timer);
  caption.textContent = 'REAKSI / ' + label.toUpperCase();
  symbol.textContent = mark;
  const next = category + '/1.gif';
  source.href = library + '/blob/main/' + next;
  if (next !== reaction) {
    reaction = next;
    refreshGif();
  }
}

gif.addEventListener('load', () => { gif.hidden = !motionOn; });
gif.addEventListener('error', () => { gif.hidden = true; });

function tone(frequency) {
  if (!soundOn || !window.AudioContext) return;
  audio ??= new AudioContext();
  audio.resume().then(() => {
    const oscillator = audio.createOscillator();
    const volume = audio.createGain();
    oscillator.type = 'triangle';
    oscillator.frequency.value = frequency;
    volume.gain.setValueAtTime(0.07, audio.currentTime);
    volume.gain.exponentialRampToValueAtTime(0.001, audio.currentTime + 0.12);
    oscillator.connect(volume).connect(audio.destination);
    oscillator.start();
    oscillator.stop(audio.currentTime + 0.12);
  }).catch(() => {});
}

function showOperator(playSound = false) {
  const [label, category, pitch] = operations[operator.value];
  show(label, category, operator.value);
  if (playSound) tone(pitch);
}

function updateSwitch(button, label, enabled) {
  button.setAttribute('aria-pressed', String(enabled));
  button.textContent = label + ': ' + (enabled ? 'ON' : 'OFF');
}

motion.addEventListener('click', () => {
  motionOn = !motionOn;
  updateSwitch(motion, 'GIF', motionOn);
  refreshGif();
});
sound.addEventListener('click', () => {
  soundOn = !soundOn;
  updateSwitch(sound, 'Suara', soundOn);
  tone(660);
});
operator.addEventListener('change', () => showOperator(true));
for (const name of ['bilangan1', 'bilangan2']) {
  form.elements[name].addEventListener('input', (event) => {
    clearTimeout(timer);
    const input = event.target;
    const digit = (event.data ?? input.value.slice(0, input.selectionStart)).match(/[0-9](?!.*[0-9])/);
    if (!digit) {
      showOperator();
      return;
    }
    const number = Number(digit[0]);
    tone(300 + number * 45);
    timer = setTimeout(() => show('Digit ' + number, digits[number], digit[0]), 140);
  });
}

updateSwitch(motion, 'GIF', motionOn);
showOperator();
const result = document.querySelector('#result');
// Result entrance uses Anime.js v3.2.2 (MIT). Calculation remains server-side.
if (window.anime && !reduceMotion.matches && result.textContent !== '—') {
  window.anime({ targets: result, translateY: [9, 0], opacity: [0, 1], duration: 430, easing: 'easeOutCubic' });
}

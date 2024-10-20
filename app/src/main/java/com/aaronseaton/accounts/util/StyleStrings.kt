package com.aaronseaton.accounts.util

val styleString = """body{margin:36pt;font: 0.8em Arial, sans-serif;}
header{
    height:72pt; 
    display:flex;
    justify-content:space-between;
    border-bottom: 1px solid lightgrey;
}
.logo{
    height: inherit;
}
.logo > img {
    height: inherit;
}
.business-info{
    color: rgb(160, 160, 160);
    font-size: 9px;
    text-align: right;
}
.information-line{
    text-align: center;
    font-weight: bold;
    font-size: 18pt;
}
.information-label{
    background-color: rgb(210,210,210);
}
.spacer{
    height:2.3in;
    display:flex; 
    justify-content:center; 
    align-items: flex-end;
    text-align: center;
    font-size: 16px;
}

footer{
    border-top: 1px solid lightgray;
    height:36pt;
    display:flex; 
    justify-content:center; 
    align-items: flex-end;
    text-align: center;
    font-size: 10px;
    color: rgb(130, 130, 130);
}
td{
    font-weight: bold;
}
td{
    color: rgb(46, 46, 46);
}
td.label{
    color: rgb(110, 110, 110);
}
table{
    border-spacing: 10pt;
    text-align: justify;
}""".trim()
val styleStringTwo = """
body{margin:36pt;font: 1.3em Arial, sans-serif; height: 792px; width: 612px;}
header{
    height:100pt; 
    display:flex;
    justify-content:space-between;
    border-bottom: 1px solid lightgrey;
}
.logo{
    height: inherit;
}
.logo > img {
    height: inherit;
}
.business-info{
    color: rgb(130, 130, 130);
    font-size: 12px;
    text-align: right;
}
.information-line{
    text-align: center;
    font-weight: bold;
    font-size: 36pt;
}
.information-label{
    background-color: rgb(210,210,210);
}
.spacer{
    height:2in;
    display:flex; 
    justify-content:center; 
    align-items: flex-end;
    text-align: center;
    font-size: 16px;
}

footer{
    border-top: 1px solid lightgray;
    height:36pt;
    display:flex; 
    justify-content:center; 
    align-items: flex-end;
    text-align: center;
    font-size: 12px;
    color: rgb(130, 130, 130);
}
td{
    font-weight: bold;
}
td{
    color: rgb(46, 46, 46);
}
td.label{
    color: rgb(110, 110, 110);
}
table{
    border-spacing: 5pt 24pt;
    text-align: justify;
    vertical-align: top;
}""".trim()
val styleStringThree = """body{margin:36pt;font: 1.5em Arial, sans-serif;}
header{
    height:144pt; 
    display:flex;
    justify-content:space-between;
    border-bottom: 1px solid lightgrey;
}
.logo{
    height: inherit;
}
.logo > img {
    height: inherit;
}
.business-info{
    color: rgb(130, 130, 130);
    font-size: 16px;
    text-align: right;
}
.information-line{
    text-align: center;
    font-weight: bold;
    font-size: 36pt;
}
.information-label{
    background-color: rgb(210,210,210);
}
.spacer{
    height:2.3in;
    display:flex; 
    justify-content:center; 
    align-items: flex-end;
    text-align: center;
    font-size: 16px;
}

footer{
    border-top: 1px solid lightgray;
    height:36pt;
    display:flex; 
    justify-content:center; 
    align-items: flex-end;
    text-align: center;
    font-size: 16px;
    color: rgb(130, 130, 130);
}
td{
    font-weight: bold;
}
td{
    color: rgb(46, 46, 46);
}
td.label{
    color: rgb(110, 110, 110);
}
table{
    border-spacing: 10pt;
    text-align: justify;
}""".trim()
val tailWindStyle = """
    /*
! tailwindcss v3.4.0 | MIT License | https://tailwindcss.com
*/

/*
1. Prevent padding and border from affecting element width. (https://github.com/mozdevs/cssremedy/issues/4)
2. Allow adding a border to an element by just adding a border-width. (https://github.com/tailwindcss/tailwindcss/pull/116)
*/

*,
::before,
::after {
  box-sizing: border-box;
  /* 1 */
  border-width: 0;
  /* 2 */
  border-style: solid;
  /* 2 */
  border-color: #e5e7eb;
  /* 2 */
}

::before,
::after {
  --tw-content: '';
}

/*
1. Use a consistent sensible line-height in all browsers.
2. Prevent adjustments of font size after orientation changes in iOS.
3. Use a more readable tab size.
4. Use the user's configured `sans` font-family by default.
5. Use the user's configured `sans` font-feature-settings by default.
6. Use the user's configured `sans` font-variation-settings by default.
7. Disable tap highlights on iOS
*/

html,
:host {
  line-height: 1.5;
  /* 1 */
  -webkit-text-size-adjust: 100%;
  /* 2 */
  -moz-tab-size: 4;
  /* 3 */
  tab-size: 4;
  /* 3 */
  font-family: ui-sans-serif, system-ui, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji";
  /* 4 */
  font-feature-settings: normal;
  /* 5 */
  font-variation-settings: normal;
  /* 6 */
  -webkit-tap-highlight-color: transparent;
  /* 7 */
}

/*
1. Remove the margin in all browsers.
2. Inherit line-height from `html` so users can set them as a class directly on the `html` element.
*/

body {
  margin: 0;
  /* 1 */
  line-height: inherit;
  /* 2 */
}

/*
1. Add the correct height in Firefox.
2. Correct the inheritance of border color in Firefox. (https://bugzilla.mozilla.org/show_bug.cgi?id=190655)
3. Ensure horizontal rules are visible by default.
*/

hr {
  height: 0;
  /* 1 */
  color: inherit;
  /* 2 */
  border-top-width: 1px;
  /* 3 */
}

/*
Add the correct text decoration in Chrome, Edge, and Safari.
*/

abbr:where([title]) {
  text-decoration: underline dotted;
}

/*
Remove the default font size and weight for headings.
*/

h1,
h2,
h3,
h4,
h5,
h6 {
  font-size: inherit;
  font-weight: inherit;
}

/*
Reset links to optimize for opt-in styling instead of opt-out.
*/

a {
  color: inherit;
  text-decoration: inherit;
}

/*
Add the correct font weight in Edge and Safari.
*/

b,
strong {
  font-weight: bolder;
}

/*
1. Use the user's configured `mono` font-family by default.
2. Use the user's configured `mono` font-feature-settings by default.
3. Use the user's configured `mono` font-variation-settings by default.
4. Correct the odd `em` font sizing in all browsers.
*/

code,
kbd,
samp,
pre {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  /* 1 */
  font-feature-settings: normal;
  /* 2 */
  font-variation-settings: normal;
  /* 3 */
  font-size: 1em;
  /* 4 */
}

/*
Add the correct font size in all browsers.
*/

small {
  font-size: 80%;
}

/*
Prevent `sub` and `sup` elements from affecting the line height in all browsers.
*/

sub,
sup {
  font-size: 75%;
  line-height: 0;
  position: relative;
  vertical-align: baseline;
}

sub {
  bottom: -0.25em;
}

sup {
  top: -0.5em;
}

/*
1. Remove text indentation from table contents in Chrome and Safari. (https://bugs.chromium.org/p/chromium/issues/detail?id=999088, https://bugs.webkit.org/show_bug.cgi?id=201297)
2. Correct table border color inheritance in all Chrome and Safari. (https://bugs.chromium.org/p/chromium/issues/detail?id=935729, https://bugs.webkit.org/show_bug.cgi?id=195016)
3. Remove gaps between table borders by default.
*/

table {
  text-indent: 0;
  /* 1 */
  border-color: inherit;
  /* 2 */
  border-collapse: collapse;
  /* 3 */
}

/*
1. Change the font styles in all browsers.
2. Remove the margin in Firefox and Safari.
3. Remove default padding in all browsers.
*/

button,
input,
optgroup,
select,
textarea {
  font-family: inherit;
  /* 1 */
  font-feature-settings: inherit;
  /* 1 */
  font-variation-settings: inherit;
  /* 1 */
  font-size: 100%;
  /* 1 */
  font-weight: inherit;
  /* 1 */
  line-height: inherit;
  /* 1 */
  color: inherit;
  /* 1 */
  margin: 0;
  /* 2 */
  padding: 0;
  /* 3 */
}

/*
Remove the inheritance of text transform in Edge and Firefox.
*/

button,
select {
  text-transform: none;
}

/*
1. Correct the inability to style clickable types in iOS and Safari.
2. Remove default button styles.
*/

button,
[type='button'],
[type='reset'],
[type='submit'] {
  -webkit-appearance: button;
  /* 1 */
  background-color: transparent;
  /* 2 */
  background-image: none;
  /* 2 */
}

/*
Use the modern Firefox focus style for all focusable elements.
*/

:-moz-focusring {
  outline: auto;
}

/*
Remove the additional `:invalid` styles in Firefox. (https://github.com/mozilla/gecko-dev/blob/2f9eacd9d3d995c937b4251a5557d95d494c9be1/layout/style/res/forms.css#L728-L737)
*/

:-moz-ui-invalid {
  box-shadow: none;
}

/*
Add the correct vertical alignment in Chrome and Firefox.
*/

progress {
  vertical-align: baseline;
}

/*
Correct the cursor style of increment and decrement buttons in Safari.
*/

::-webkit-inner-spin-button,
::-webkit-outer-spin-button {
  height: auto;
}

/*
1. Correct the odd appearance in Chrome and Safari.
2. Correct the outline style in Safari.
*/

[type='search'] {
  -webkit-appearance: textfield;
  /* 1 */
  outline-offset: -2px;
  /* 2 */
}

/*
Remove the inner padding in Chrome and Safari on macOS.
*/

::-webkit-search-decoration {
  -webkit-appearance: none;
}

/*
1. Correct the inability to style clickable types in iOS and Safari.
2. Change font properties to `inherit` in Safari.
*/

::-webkit-file-upload-button {
  -webkit-appearance: button;
  /* 1 */
  font: inherit;
  /* 2 */
}

/*
Add the correct display in Chrome and Safari.
*/

summary {
  display: list-item;
}

/*
Removes the default spacing and border for appropriate elements.
*/

blockquote,
dl,
dd,
h1,
h2,
h3,
h4,
h5,
h6,
hr,
figure,
p,
pre {
  margin: 0;
}

fieldset {
  margin: 0;
  padding: 0;
}

legend {
  padding: 0;
}

ol,
ul,
menu {
  list-style: none;
  margin: 0;
  padding: 0;
}

/*
Reset default styling for dialogs.
*/

dialog {
  padding: 0;
}

/*
Prevent resizing textareas horizontally by default.
*/

textarea {
  resize: vertical;
}

/*
1. Reset the default placeholder opacity in Firefox. (https://github.com/tailwindlabs/tailwindcss/issues/3300)
2. Set the default placeholder color to the user's configured gray 400 color.
*/

input::placeholder,
textarea::placeholder {
  opacity: 1;
  /* 1 */
  color: #9ca3af;
  /* 2 */
}

/*
Set the default cursor for buttons.
*/

button,
[role="button"] {
  cursor: pointer;
}

/*
Make sure disabled buttons don't get the pointer cursor.
*/

:disabled {
  cursor: default;
}

/*
1. Make replaced elements `display: block` by default. (https://github.com/mozdevs/cssremedy/issues/14)
2. Add `vertical-align: middle` to align replaced elements more sensibly by default. (https://github.com/jensimmons/cssremedy/issues/14#issuecomment-634934210)
   This can trigger a poorly considered lint error in some tools but is included by design.
*/

img,
svg,
video,
canvas,
audio,
iframe,
embed,
object {
  display: block;
  /* 1 */
  vertical-align: middle;
  /* 2 */
}

/*
Constrain images and videos to the parent width and preserve their intrinsic aspect ratio. (https://github.com/mozdevs/cssremedy/issues/14)
*/

img,
video {
  max-width: 100%;
  height: auto;
}

/* Make elements with the HTML hidden attribute stay hidden by default */

[hidden] {
  display: none;
}

*, ::before, ::after{
  --tw-border-spacing-x: 0;
  --tw-border-spacing-y: 0;
  --tw-translate-x: 0;
  --tw-translate-y: 0;
  --tw-rotate: 0;
  --tw-skew-x: 0;
  --tw-skew-y: 0;
  --tw-scale-x: 1;
  --tw-scale-y: 1;
  --tw-pan-x:  ;
  --tw-pan-y:  ;
  --tw-pinch-zoom:  ;
  --tw-scroll-snap-strictness: proximity;
  --tw-gradient-from-position:  ;
  --tw-gradient-via-position:  ;
  --tw-gradient-to-position:  ;
  --tw-ordinal:  ;
  --tw-slashed-zero:  ;
  --tw-numeric-figure:  ;
  --tw-numeric-spacing:  ;
  --tw-numeric-fraction:  ;
  --tw-ring-inset:  ;
  --tw-ring-offset-width: 0px;
  --tw-ring-offset-color: #fff;
  --tw-ring-color: rgb(59 130 246 / 0.5);
  --tw-ring-offset-shadow: 0 0 #0000;
  --tw-ring-shadow: 0 0 #0000;
  --tw-shadow: 0 0 #0000;
  --tw-shadow-colored: 0 0 #0000;
  --tw-blur:  ;
  --tw-brightness:  ;
  --tw-contrast:  ;
  --tw-grayscale:  ;
  --tw-hue-rotate:  ;
  --tw-invert:  ;
  --tw-saturate:  ;
  --tw-sepia:  ;
  --tw-drop-shadow:  ;
  --tw-backdrop-blur:  ;
  --tw-backdrop-brightness:  ;
  --tw-backdrop-contrast:  ;
  --tw-backdrop-grayscale:  ;
  --tw-backdrop-hue-rotate:  ;
  --tw-backdrop-invert:  ;
  --tw-backdrop-opacity:  ;
  --tw-backdrop-saturate:  ;
  --tw-backdrop-sepia:  
}

::backdrop{
  --tw-border-spacing-x: 0;
  --tw-border-spacing-y: 0;
  --tw-translate-x: 0;
  --tw-translate-y: 0;
  --tw-rotate: 0;
  --tw-skew-x: 0;
  --tw-skew-y: 0;
  --tw-scale-x: 1;
  --tw-scale-y: 1;
  --tw-pan-x:  ;
  --tw-pan-y:  ;
  --tw-pinch-zoom:  ;
  --tw-scroll-snap-strictness: proximity;
  --tw-gradient-from-position:  ;
  --tw-gradient-via-position:  ;
  --tw-gradient-to-position:  ;
  --tw-ordinal:  ;
  --tw-slashed-zero:  ;
  --tw-numeric-figure:  ;
  --tw-numeric-spacing:  ;
  --tw-numeric-fraction:  ;
  --tw-ring-inset:  ;
  --tw-ring-offset-width: 0px;
  --tw-ring-offset-color: #fff;
  --tw-ring-color: rgb(59 130 246 / 0.5);
  --tw-ring-offset-shadow: 0 0 #0000;
  --tw-ring-shadow: 0 0 #0000;
  --tw-shadow: 0 0 #0000;
  --tw-shadow-colored: 0 0 #0000;
  --tw-blur:  ;
  --tw-brightness:  ;
  --tw-contrast:  ;
  --tw-grayscale:  ;
  --tw-hue-rotate:  ;
  --tw-invert:  ;
  --tw-saturate:  ;
  --tw-sepia:  ;
  --tw-drop-shadow:  ;
  --tw-backdrop-blur:  ;
  --tw-backdrop-brightness:  ;
  --tw-backdrop-contrast:  ;
  --tw-backdrop-grayscale:  ;
  --tw-backdrop-hue-rotate:  ;
  --tw-backdrop-invert:  ;
  --tw-backdrop-opacity:  ;
  --tw-backdrop-saturate:  ;
  --tw-backdrop-sepia:  
}

.mt-8{
  margin-top: 2rem;
}

.mr-8{
  margin-right: 2rem
}

.flex{
  display: flex
}

.aspect-\[8\.5\/11\]{
  aspect-ratio: 8.5/11
}

.w-24{
  width: 6rem
}

.w-\[20\%\]{
  width: 20%
}

.w-\[60\%\]{
  width: 60%
}

.w-full{
  width: 100%
}

.flex-1{
  flex: 1 1 0%
}

.cursor-pointer{
  cursor: pointer
}

.select-none{
  user-select: none
}

.flex-row{
  flex-direction: row
}

.flex-col{
  flex-direction: column
}

.justify-between{
  justify-content: space-between
}

.gap-4{
  gap: 1rem
}

.gap-6{
  gap: 1.5rem
}

.gap-8{
  gap: 2rem
}

.rounded-lg{
  border-radius: 0.5rem
}

.border{
  border-width: 1px
}

.border-neutral-100{
  --tw-border-opacity: 1;
  border-color: rgb(245 245 245 / var(--tw-border-opacity))
}

.bg-blue-50{
  --tw-bg-opacity: 1;
  background-color: rgb(239 246 255 / var(--tw-bg-opacity))
}

.bg-white{
  --tw-bg-opacity: 1;
  background-color: rgb(255 255 255 / var(--tw-bg-opacity))
}

.p-2{
  padding: 0.5rem;
}
.p-12{
  padding: 3rem;
}

.px-6{
  padding-left: 1.5rem;
  padding-right: 1.5rem
}

.pt-2{
  padding-top: 0.5rem
}

.text-center{
  text-align: center
}

.text-right{
  text-align: right
}

.font-\[\'Brush_Script_MT\'\]{
  font-family: 'Brush Script MT'
}

.font-\[Helvetica-Bold\]{
  font-family: Helvetica-Bold
}

.text-3xl{
  font-size: 1.875rem;
  line-height: 2.25rem
}

.text-lg{
  font-size: 1.125rem;
  line-height: 1.75rem
}

.text-xl{
  font-size: 1.25rem;
  line-height: 1.75rem
}

.uppercase{
  text-transform: uppercase
}

.leading-5{
  line-height: 1.25rem
}

.text-black{
  --tw-text-opacity: 1;
  color: rgb(0 0 0 / var(--tw-text-opacity))
}

.text-blue-800{
  --tw-text-opacity: 1;
  color: rgb(30 64 175 / var(--tw-text-opacity))
}

.text-neutral-500{
  --tw-text-opacity: 1;
  color: rgb(115 115 115 / var(--tw-text-opacity))
}

.text-neutral-600{
  --tw-text-opacity: 1;
  color: rgb(82 82 82 / var(--tw-text-opacity))
}

.shadow-md{
  --tw-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  --tw-shadow-colored: 0 4px 6px -1px var(--tw-shadow-color), 0 2px 4px -2px var(--tw-shadow-color);
  box-shadow: var(--tw-ring-offset-shadow, 0 0 #0000), var(--tw-ring-shadow, 0 0 #0000), var(--tw-shadow)
}

.shadow-neutral-700{
  --tw-shadow-color: #404040;
  --tw-shadow: var(--tw-shadow-colored)
}

@media print{
  .print\:hidden{
    display: none
  }
}

@media (min-width: 640px){
  .sm\:w-auto{
    width: auto
  }

  .sm\:min-w-\[700px\]{
    min-width: 700px
  }

  .sm\:max-w-\[750px\]{
    max-width: 750px
  }

  .sm\:p-12{
    padding: 3rem
  }
}

@media (prefers-color-scheme: dark){
  .dark\:border-neutral-900{
    --tw-border-opacity: 1;
    border-color: rgb(23 23 23 / var(--tw-border-opacity))
  }

  .dark\:bg-neutral-700{
    --tw-bg-opacity: 1;
    background-color: rgb(64 64 64 / var(--tw-bg-opacity))
  }

  .dark\:bg-neutral-950{
    --tw-bg-opacity: 1;
    background-color: rgb(10 10 10 / var(--tw-bg-opacity))
  }

  .dark\:text-neutral-100{
    --tw-text-opacity: 1;
    color: rgb(245 245 245 / var(--tw-text-opacity))
  }

  .dark\:text-neutral-300{
    --tw-text-opacity: 1;
    color: rgb(212 212 212 / var(--tw-text-opacity))
  }

  .dark\:text-white{
    --tw-text-opacity: 1;
    color: rgb(255 255 255 / var(--tw-text-opacity))
  }
}

""".trim()

# MeteoSystem

An Android application which displays meteo information from few APRS weather stations. Now mostly from Beskids mountins in Poland, but let's hope that it will widespread a little bit firther ;) The app was initialy called Pogoda.cc but then it was renamed to something more suitable for international use. 

Google Play link to this software: https://play.google.com/store/apps/details?id=cc.pogoda.mobile.meteosystem

A list of currently avaliable weather stations (with APRS callsign in brackets) are below, with english or german site name after the comma. As for now all of them are located in southern Poland, mostly in Beskids Mountains but also in Low Beskids (located in SW Poland) and Oppelner Schlesien land.

- Skrzyczne (SR9NSK), Rauhkogel
- Jaworzyna Skrzyczneńska (SR9NSK-5)
- Kozia Góra (SR9WXS), Ziegenbock
- Magurka Wilkowicka (SR9WXM)
- Bezmiechowa Górna (SR8WXB)
- Góra Chełm (SR9WXG)
- Dukla, Wzgórze 534 (SR8WXD)
- Polska Nowa Wieś (SR6WXP), Polnisch Neudorf
- Leskowiec (SR9WXL)
- Markowe Szczawiny (SR9WXM)
- Jezioro Żywieckie (SR9WXZ)
- Międzybrodzie Żywieckie

This application is a part of bigger weather station system. The rest of it is formed by:
API: https://github.com/SP8EBC/meteo_backend
Receiving data from stations via APRS and storing in DB: https://github.com/SP8EBC/aprs2rrd-se
Firmware for APRS weather station controller and it's design: https://github.com/SP8EBC/ParaTNC

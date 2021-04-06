# IPHubAPI Mod v1.1 for Forge 1.8.8 & 1.8.9
* On first time use, execute the command **/setiphubkey [IPHub API Key]** to set the API key used by your client. This key is saved in a config file, meaning it does not reset on client launch.
  ![SetIPHubKey](https://user-images.githubusercontent.com/53131588/106963056-e1b94180-66f4-11eb-8def-d4224d3e6a94.png)
  
  A Free IPHub API Key can be generated [here](https://iphub.info/apiKey/newFree) with any IPHub account with a verified email. If your account already has an API Key, you can view and copy the key from [here](https://iphub.info/account).
  ![copyAPIKey](https://user-images.githubusercontent.com/53131588/106964698-5db48900-66f7-11eb-8ee0-f02183fcc1d5.png)

* This mod provides automatic IP checks using the IPHub API
  ![IPChecking](https://user-images.githubusercontent.com/53131588/106961661-c5b4a080-66f2-11eb-8514-e6e26900722b.png)
  * Currently supports /iphistory aka /iphist (SaiCo LiteBans), /dupeip aka /alts (SaiCo LiteBans), /eseen (SaiCo Essentials), /seen (SaiCo)
  * Uses caching for automatic IP checks. This cache is cleared once the client is closed. This typically speeds up the query of large IP histories of multiple related accounts.
  * Automatic IP checks can be toggled with the command **/toggleipcheck**. Automatic IP Checking is enabled by default on client launch.
  ![ToggleIPCheckCommand](https://user-images.githubusercontent.com/53131588/106962720-648dcc80-66f4-11eb-97ab-6b2257c026e6.png)

* Use the **/ipinfo [IP]** command to provide information about an IP using the IPHub API

   ![IPInfoCommand](https://user-images.githubusercontent.com/53131588/106965028-e7645680-66f7-11eb-809a-cda95f82e516.png)

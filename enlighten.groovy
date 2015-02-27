/**
 *  Enlighten Solar System
 *
 *  Copyright 2015 Umesh
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
preferences {
    input("user_id", "text", title: "Enphase User ID", description: "Enphase User Id")
    input("system_id", "text", title: "Enphase System ID", description: "Enphase System Id")
    
}
metadata {
	definition (name: "Enlighten Solar System", namespace: "usirsiwal", author: "Umesh Sirsiwal") {
		capability "Power Meter"
        capability "Refresh"
	    capability "Polling"

	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
            valueTile("energy_today", "device.energy_today", width: 2, height: 2, canChangeIcon: true) {
   	         state("energy_today", label: '${currentValue}KWh', unit:"KWh", backgroundColors: [
                    [value: 2, color: "#bc2323"],
                    [value: 5, color: "#d04e00"],
                    [value: 10, color: "#f1d801"],
                    [value: 20, color: "#90d2a7"],
					[value: 30, color: "#44b621"],
                    [value: 40, color: "#1e9cbb"],
                    [value: 50, color: "#153591"]
    	            ]
            	)
        	}    
            valueTile("energy_life", "device.energy_life", width: 1, height: 1, canChangeIcon: true) {
   	         state("energy_life", label: '${currentValue}MWh', unit:"MWh", backgroundColors: [
                    [value: 2, color: "#bc2323"],
                    [value: 5, color: "#d04e00"],
                    [value: 10, color: "#f1d801"],
                    [value: 20, color: "#90d2a7"],
					[value: 30, color: "#44b621"],
                    [value: 40, color: "#1e9cbb"],
                    [value: 50, color: "#153591"],
    	            ]
            	)
        	}    

            standardTile("refresh", "device.energy_today", inactiveLabel: false, decoration: "flat") {
                state "default", action:"polling.poll", icon:"st.secondary.refresh"
            }

        
        main "energy_today"
        details(["energy_today", "energy_life", "refresh"])

	}
}


// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"

}

def poll() {
	refresh()
}

def refresh() {
  log.debug "Executing 'refresh'"
  energyRefresh()
}


def energyRefresh() {  
  log.debug "Executing 'energyToday'"    
  
  httpGet("https://api.enphaseenergy.com/api/v2/systems/${settings.system_id}/summary?key=2bff33c35ee9c0b88c439cbe400e52c6&user_id=${settings.user_id}") {resp ->
        if (resp.data) {
        	log.debug "${resp.data}"
            def energyToday = resp.data.energy_today
            def energyLife = resp.data.energy_lifetime
            
            log.debug "Energy today ${energyToday}"
            log.debug "Energy life ${energyLife}"
            
            sendEvent(name: 'energy_today', value: (energyToday/1000))
            sendEvent(name: 'energy_life', value: (energyLife/1000000))


        }
        if(resp.status == 200) {
            	log.debug "poll results returned"
        }
         else {
            log.error "polling children & got http status ${resp.status}"
        }
    }
}

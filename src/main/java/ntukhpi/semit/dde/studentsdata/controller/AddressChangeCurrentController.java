package ntukhpi.semit.dde.studentsdata.controller;

import ntukhpi.semit.dde.studentsdata.entity.Address;
import ntukhpi.semit.dde.studentsdata.entity.Person;
import ntukhpi.semit.dde.studentsdata.entity.PhoneNumber;
import ntukhpi.semit.dde.studentsdata.service.interf.AddressService;
import ntukhpi.semit.dde.studentsdata.service.interf.PersonService;
import ntukhpi.semit.dde.studentsdata.service.interf.PhoneNumberService;
import ntukhpi.semit.dde.studentsdata.utils.ContactMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Controller
@RequestMapping("/address_current")
public class AddressChangeCurrentController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private PersonService personService;

    @GetMapping
    public String changeCurrentAddress(@RequestParam("id_address") Long idAddress,
                                       @RequestParam("id_owner") Long idOwner,
                                       Model model) {
        System.out.println("AddressChangeCurrentController#changeCurrentAddress");

        Person owner = personService.getPersonById(idOwner);
        Address address = addressService.getAddressById(idAddress);

        Address currentAddress = owner.getCurrentAddress();
        Boolean newCurrentValue = !owner.getCurrent(address);

        boolean updateRes = false;

        if (currentAddress != null && !currentAddress.equals(address)) {
            owner.setCurrent(currentAddress, !owner.getCurrent(currentAddress));
            updateRes = addressService.updateAddress(address);

            if (!updateRes) {
                System.out.println("Error resetting the current address! Update SQL mistake!!!");
                model.addAttribute("error", ContactMessages.MESSAGE04.getText());
                model.addAttribute("owner", owner);
                return "addresses_person";
            }
        }

        owner.setCurrent(address, newCurrentValue);
        updateRes = addressService.updateAddress(address);

        if (updateRes) {
            if (address.equals(currentAddress) && !newCurrentValue) {
                System.out.println("\"Main\" address removed!");
                model.addAttribute("messageCode", 6);
            } else {
                System.out.println("\"Main\" address changed!");
                model.addAttribute("messageCode", 7);
            }
        } else {
            System.out.println("Update error! Check for the presence of an address with the status \"Main\"! Update SQL mistake!!!");
            model.addAttribute("error", ContactMessages.MESSAGE08.getText());
        }

        model.addAttribute("owner", owner);
        return "redirect:/addresses?id_owner=" + idOwner;
    }

    @PostMapping
    public String doPost(@RequestParam("id_address") Long idAddress,
                         @RequestParam("id_owner") Long idOwner,
                         Model model) {
        return changeCurrentAddress(idAddress, idOwner, model);
    }
}

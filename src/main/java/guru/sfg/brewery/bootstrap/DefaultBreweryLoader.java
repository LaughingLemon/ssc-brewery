/*
 *  Copyright 2020 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerInventory;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.BeerOrderLine;
import guru.sfg.brewery.domain.Brewery;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.domain.OrderStatusEnum;
import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.BreweryRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by jt on 2019-01-26.
 */
@RequiredArgsConstructor
@Component
public class DefaultBreweryLoader implements CommandLineRunner {

    public static final String TASTING_ROOM = "Tasting Room";
    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    public static final String ODDBINS_DISTIBUTOR = "Oddbins";
    public static final String WINE_WAREHOUSE_DISTIBUTOR = "Wine Warehouse";

    public static final int CREATE = 0;
    public static final int READ = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private Role adminRole;
    private Role userRole;
    private Role customerRole;

    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private Beer mangoBobs;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    @Override
    public void run(String... args) {
        loadAuthorityData();
        loadUserData();
        loadBreweryData();
        loadTastingRoomData();
        loadCustomerData();
    }

    private void loadCustomerData() {
        Customer oddbinsCustomer = customerRepository.save(Customer.builder()
                                                                   .customerName(ODDBINS_DISTIBUTOR)
                                                                   .apiKey(UUID.randomUUID())
                                                                   .build());
        Customer wineWarehouseCustomer = customerRepository.save(Customer.builder()
                                                                         .customerName(WINE_WAREHOUSE_DISTIBUTOR)
                                                                         .apiKey(UUID.randomUUID())
                                                                         .build());
        userRepository.save(User.builder()
                                    .username("oddbinsUser")
                                    .password(passwordEncoder.encode("oddbins"))
                                .customer(oddbinsCustomer)
                                    .role(customerRole)
                                .build());
        userRepository.save(User.builder()
                                .username("wineUser")
                                .password(passwordEncoder.encode("winewarehouse"))
                                .customer(wineWarehouseCustomer)
                                    .role(customerRole)
                                .build());
        createCustomerOrder(oddbinsCustomer);
        createCustomerOrder(wineWarehouseCustomer);
    }

    private void createCustomerOrder(Customer customer) {
        beerOrderRepository.save(BeerOrder.builder()
                                          .customer(customer)
                                          .orderStatus(OrderStatusEnum.NEW)
                                          .beerOrderLines(Set.of(BeerOrderLine.builder()
                                                                              .orderQuantity(3)
                                                                              .beer(mangoBobs)
                                                                              .build()))
                                          .build());
    }

    private void loadTastingRoomData() {
        Customer tastingRoom = Customer.builder()
                                       .customerName(TASTING_ROOM)
                                       .apiKey(UUID.randomUUID())
                                       .build();

        customerRepository.save(tastingRoom);

        beerRepository.findAll()
                      .forEach(beer -> beerOrderRepository.save(BeerOrder.builder()
                                                                         .customer(tastingRoom)
                                                                         .orderStatus(OrderStatusEnum.NEW)
                                                                         .beerOrderLines(Set.of(BeerOrderLine.builder()
                                                                                                             .beer(beer)
                                                                                                             .orderQuantity(
                                                                                                                     2)
                                                                                                             .build()))
                                                                         .build()));
    }

    private void loadBreweryData() {
        if (breweryRepository.count() == 0) {
            breweryRepository.save(Brewery
                                           .builder()
                                           .breweryName("Cage Brewing")
                                           .build());

            mangoBobs = Beer.builder()
                            .beerName("Mango Bobs")
                            .beerStyle(BeerStyleEnum.IPA)
                            .minOnHand(12)
                            .quantityToBrew(200)
                            .upc(BEER_1_UPC)
                            .build();

            beerRepository.save(mangoBobs);
            beerInventoryRepository.save(BeerInventory.builder()
                                                      .beer(mangoBobs)
                                                      .quantityOnHand(500)
                                                      .build());

            Beer galaxyCat = Beer.builder()
                                 .beerName("Galaxy Cat")
                                 .beerStyle(BeerStyleEnum.PALE_ALE)
                                 .minOnHand(12)
                                 .quantityToBrew(200)
                                 .upc(BEER_2_UPC)
                                 .build();

            beerRepository.save(galaxyCat);
            beerInventoryRepository.save(BeerInventory.builder()
                                                      .beer(galaxyCat)
                                                      .quantityOnHand(500)
                                                      .build());

            Beer pinball = Beer.builder()
                               .beerName("Pinball Porter")
                               .beerStyle(BeerStyleEnum.PORTER)
                               .minOnHand(12)
                               .quantityToBrew(200)
                               .upc(BEER_3_UPC)
                               .build();

            beerRepository.save(pinball);
            beerInventoryRepository.save(BeerInventory.builder()
                                                      .beer(pinball)
                                                      .quantityOnHand(500)
                                                      .build());

        }
    }

    private void loadUserData() {
        userRepository.save(User.builder()
                                .username("spring")
                                .password("{bcrypt15}$2a$15$PQNH5woatbMfrWrgvKvW6ekkSvkA08IB2CBKUjMqx.e/Hv4lTO/ZS")
                                .role(adminRole)
                                .build());

        userRepository.save(User.builder()
                                .username("user")
                                .password("{bcrypt}$2a$10$7aLHzJ.ywdrlihAwgopoa.4utl4iFHGSAJ13AMspyASltxlZbAQu.")
                                .role(userRole)
                                .build());

        userRepository.save(User.builder()
                                .username("scott")
                                .password("{noop}password")
                                .role(customerRole)
                                .build());
    }

    private Authority[] buildCRUDAuthSet(String prefix) {
        Authority[] authorities = new Authority[4];
        authorities[CREATE] = authorityRepository.save(Authority.builder()
                                                                .permission(prefix + ".create")
                                                                .build());
        authorities[READ] = authorityRepository.save(Authority.builder()
                                                              .permission(prefix + ".read")
                                                              .build());
        authorities[UPDATE] = authorityRepository.save(Authority.builder()
                                                                .permission(prefix + ".update")
                                                                .build());
        authorities[DELETE] = authorityRepository.save(Authority.builder()
                                                                .permission(prefix + ".delete")
                                                                .build());
        return authorities;
    }

    private void loadAuthorityData() {
        Authority[] beerAuthorities = buildCRUDAuthSet("beer");
        Authority[] customerAuthorities = buildCRUDAuthSet("customer");
        Authority[] breweryAuthorities = buildCRUDAuthSet("brewery");
        Authority[] orderAuthorities = buildCRUDAuthSet("order");
        Authority[] customerOrderAuthorities = buildCRUDAuthSet("customer.order");

        adminRole = roleRepository.save(Role.builder()
                                            .name("ADMIN")
                                            .build());
        userRole = roleRepository.save(Role.builder()
                                           .name("USER")
                                           .build());
        customerRole = roleRepository.save(Role.builder()
                                               .name("CUSTOMER")
                                               .build());

        Set<Authority> adminAuths = Stream.of(beerAuthorities,
                                              customerAuthorities,
                                              breweryAuthorities,
                                              orderAuthorities)
                                          .flatMap(Arrays::stream)
                                          .collect(Collectors.toSet());

        adminRole.setAuthorities(new HashSet<>(adminAuths));
        userRole.setAuthorities(new HashSet<>(Set.of(beerAuthorities[READ])));
        customerRole.setAuthorities(new HashSet<>(Set.of(beerAuthorities[READ],
                                                         customerAuthorities[READ],
                                                         breweryAuthorities[READ])));
        customerRole.getAuthorities()
                    .addAll(Set.of(customerOrderAuthorities));

        roleRepository.saveAll(List.of(adminRole, customerRole, userRole));
    }

}

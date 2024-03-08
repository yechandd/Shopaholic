package uk.joshiejack.shopaholic.data.shop;

import net.minecraft.resources.ResourceLocation;
import uk.joshiejack.shopaholic.api.shop.Condition;
import uk.joshiejack.shopaholic.world.shop.Department;
import uk.joshiejack.shopaholic.world.shop.input.InputMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ShopBuilder {
    protected final ResourceLocation id;
    protected Vendor vendor = Vendor.NONE;
    protected InputMethod input = InputMethod.RIGHT_CLICK;
    protected final List<Condition> conditions = new ArrayList<>();
    protected final List<DepartmentBuilder> departments = new ArrayList<>();
    protected String bg = "default";
    protected String ex = "default";

    public ShopBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static ShopBuilder of(ResourceLocation id) {
        return new ShopBuilder(id);
    }

    public ShopBuilder vendor(Vendor vendor) {
        this.vendor = vendor;
        return this;
    }

    public ShopBuilder openWith(InputMethod method) {
        this.input = method;
        return this;
    }

    public ShopBuilder department(DepartmentBuilder department) {
        this.departments.add(department);
        return this;
    }

    public ShopBuilder condition(Condition condition) {
        this.conditions.add(condition);
        return this;
    }

    public ShopBuilder background(ResourceLocation background) {
        this.bg = background.toString();
        return this;
    }

    public ShopBuilder extra(ResourceLocation extra) {
        this.ex = extra.toString();
        return this;
    }

    public List<Condition> buildConditions() {
        return conditions;
    }

    public void save(Map<ResourceLocation, Department> registry) {
        departments.forEach(departmentBuilder -> registry.put(departmentBuilder.id, new Department(id, vendor.type, vendor.data, input,
                departmentBuilder.name, departmentBuilder.outofstock, departmentBuilder.icon, Stream.concat(buildConditions().stream(), departmentBuilder.buildConditions().stream()).toList(), departmentBuilder.buildListings())));
    }
}

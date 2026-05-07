// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class sail_boat<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "sail_boat"), "main");
	private final ModelPart front;
	private final ModelPart left;
	private final ModelPart right;
	private final ModelPart back;
	private final ModelPart bottom;
	private final ModelPart left_paddle;
	private final ModelPart right_paddle;
	private final ModelPart banner_sail;
	private final ModelPart banner_panel;
	private final ModelPart water_patch;
	private final ModelPart chest_lid;
	private final ModelPart chest_knob;
	private final ModelPart chest_base;

	public sail_boat(ModelPart root) {
		this.front = root.getChild("front");
		this.left = root.getChild("left");
		this.right = root.getChild("right");
		this.back = root.getChild("back");
		this.bottom = root.getChild("bottom");
		this.left_paddle = root.getChild("left_paddle");
		this.right_paddle = root.getChild("right_paddle");
		this.banner_sail = root.getChild("banner_sail");
		this.banner_panel = root.getChild("banner_panel");
		this.water_patch = root.getChild("water_patch");
		this.chest_lid = root.getChild("chest_lid");
		this.chest_knob = root.getChild("chest_knob");
		this.chest_base = root.getChild("chest_base");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition front = partdefinition.addOrReplaceChild("front", CubeListBuilder.create().texOffs(62, 44).addBox(-9.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 22.0F, -15.0F, 0.0F, -3.1416F, 0.0F));

		PartDefinition left = partdefinition.addOrReplaceChild("left", CubeListBuilder.create().texOffs(8, 55).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 22.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition right = partdefinition.addOrReplaceChild("right", CubeListBuilder.create().texOffs(62, 36).addBox(-14.0F, -7.0F, -1.0F, 28.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 22.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition back = partdefinition.addOrReplaceChild("back", CubeListBuilder.create().texOffs(68, 52).addBox(-13.0F, -7.0F, -1.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 22.0F, 15.0F));

		PartDefinition bottom = partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 36).addBox(-14.0F, -9.0F, -3.0F, 28.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 21.0F, 0.0F, 0.0F, 1.5708F, -1.5708F));

		PartDefinition left_paddle = partdefinition.addOrReplaceChild("left_paddle", CubeListBuilder.create().texOffs(8, 63).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(68, 83).addBox(-1.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, 13.0F, -3.0F, 2.1642F, 0.8727F, 2.8798F));

		PartDefinition right_paddle = partdefinition.addOrReplaceChild("right_paddle", CubeListBuilder.create().texOffs(48, 63).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(84, 83).addBox(0.001F, -3.0F, 8.0F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, 13.0F, -3.0F, 2.1642F, -0.8727F, -2.8798F));

		PartDefinition banner_sail = partdefinition.addOrReplaceChild("banner_sail", CubeListBuilder.create().texOffs(0, 55).addBox(-1.0F, -17.25F, -1.625F, 2.0F, 60.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(68, 60).addBox(-7.0F, -17.25F, -0.625F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(38, 83).addBox(-7.0F, 7.75F, -0.625F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -18.75F, 11.375F, 0.0F, 3.1416F, 0.0F));

		PartDefinition banner_panel = partdefinition.addOrReplaceChild("banner_panel", CubeListBuilder.create().texOffs(8, 83).addBox(-7.0F, -13.0F, -0.5F, 14.0F, 26.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -23.0F, 10.5F));

		PartDefinition water_patch = partdefinition.addOrReplaceChild("water_patch", CubeListBuilder.create().texOffs(3, 2).addBox(-8.0F, -1.0F, -15.0F, 16.0F, 2.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 1.0F));

		PartDefinition chest_lid = partdefinition.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 0.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 9.0F, 2.0F));

		PartDefinition chest_knob = partdefinition.addOrReplaceChild("chest_knob", CubeListBuilder.create().texOffs(0, 59).addBox(0.0F, 0.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 12.0F, 1.0F));

		PartDefinition chest_base = partdefinition.addOrReplaceChild("chest_base", CubeListBuilder.create().texOffs(0, 76).addBox(0.0F, 0.0F, 0.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 13.0F, 2.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		front.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		back.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bottom.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_paddle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_paddle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		banner_sail.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		banner_panel.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		water_patch.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		chest_lid.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		chest_knob.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		chest_base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}